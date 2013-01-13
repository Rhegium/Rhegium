package org.rhegium.servlet.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rhegium.servlet.api.DispatchedAction;
import org.rhegium.servlet.api.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRequestDispatcher implements RequestDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultRequestDispatcher.class);

	private static final String PATH_SEPERATOR = "/";

	private static final int WEIGHT_DIRECT = 2;
	private static final int WEIGHT_WILDCARD = 1;

	private final Map<String, DispatchedAction> dispatchedActions = new HashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public void dispatch(String requestUri, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Lock l = lock.readLock();
		try {
			DispatchedAction action = null;
			int actionWeight = Integer.MIN_VALUE;

			Iterator<Entry<String, DispatchedAction>> iterator = dispatchedActions.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, DispatchedAction> entry = iterator.next();
				int weight = weightPattern(entry.getKey(), requestUri);

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("DispatchedAction with pattern %s finished in a weight of %d", entry.getKey(), weight));
				}

				if (actionWeight > weight) {
					actionWeight = weight;
					action = entry.getValue();
				}
			}

			if (action == null) {
				throw new ServletException("No DispatchedAction was found for path " + requestUri);
			}

			action.handleRequest(request, response);
		}
		finally {
			l.unlock();
		}
	}

	@Override
	public void registerDispatchedAction(String pattern, DispatchedAction dispatchedAction) {
		Lock l = lock.writeLock();
		try {
			dispatchedActions.put(pattern, dispatchedAction);
		}
		finally {
			l.unlock();
		}
	}

	@Override
	public void removeDispatchedAction(String pattern) {
		Lock l = lock.writeLock();
		try {
			dispatchedActions.remove(pattern);
		}
		finally {
			l.unlock();
		}
	}

	@Override
	public void removeDispatchedAction(DispatchedAction dispatchedAction) {
		Lock l = lock.writeLock();
		try {
			Iterator<Entry<String, DispatchedAction>> iterator = dispatchedActions.entrySet().iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getValue() == dispatchedAction) {
					iterator.remove();
					break;
				}
			}
		}
		finally {
			l.unlock();
		}
	}

	/*
	 * This is based on Springs org.springframework.util.AntPathMatcher but
	 * changed to weight the result of the matching to find best matching
	 * pattern
	 */
	private int weightPattern(String pattern, String requestUri) {
		if (requestUri.startsWith(PATH_SEPERATOR) != pattern.startsWith(PATH_SEPERATOR)) {
			return -1;
		}

		String[] patternTokens = tokenize(pattern, PATH_SEPERATOR);
		String[] uriTokens = tokenize(requestUri, PATH_SEPERATOR);

		int patternIndexStart = 0;
		int patternIndexEnd = patternTokens.length - 1;

		int uriIndexStart = 0;
		int uriIndexEnd = uriTokens.length - 1;

		int weight = 0;

		// Match all elements up to the first **
		while (patternIndexStart <= patternIndexEnd && uriIndexStart <= uriIndexEnd) {
			String token = patternTokens[patternIndexStart];
			if ("**".equals(token)) {
				weight += WEIGHT_WILDCARD;
				break;
			}

			if (!token.equals(uriTokens[uriIndexStart])) {
				return -1;
			}

			patternIndexStart++;
			uriIndexStart++;

			weight += WEIGHT_DIRECT;
		}

		if (uriIndexStart > uriIndexEnd) {
			if (patternIndexStart > patternIndexEnd) {
				if (pattern.endsWith(PATH_SEPERATOR) ? requestUri.endsWith(PATH_SEPERATOR) : !requestUri.endsWith(PATH_SEPERATOR)) {
					return weight;
				}

				if (patternIndexStart == patternIndexEnd && patternTokens[patternIndexStart].equals("*")
						&& requestUri.endsWith(PATH_SEPERATOR)) {

					return (weight += WEIGHT_WILDCARD);
				}

				for (int i = patternIndexStart; i < patternIndexEnd; i++) {
					if (!patternTokens[i].equals("**")) {
						return -1;
					}

					weight += WEIGHT_WILDCARD;
				}
			}

			return weight;
		}
		else if (patternIndexStart > patternIndexEnd) {
			return -1;
		}
		else if ("**".equals(patternTokens[patternIndexStart])) {
			return (weight += WEIGHT_WILDCARD);
		}

		while (patternIndexStart != patternIndexEnd && uriIndexStart <= uriIndexEnd) {
			int uriIndexTemp = -1;

			for (int i = patternIndexStart + 1; i <= patternIndexEnd; i++) {
				if ("**".equals(patternTokens[i])) {
					uriIndexTemp = i;
					weight += WEIGHT_WILDCARD;
					break;
				}
			}

			if (uriIndexTemp == patternIndexStart + 1) {
				patternIndexStart++;
				continue;
			}

			int uriLength = (uriIndexTemp - patternIndexStart - 1);
			int stringLength = (uriIndexEnd - uriIndexStart + 1);
			int foundIndex = -1;

			for (int i = 0; i < stringLength - uriIndexEnd; i++) {
				if (!matchPattern(patternTokens, uriTokens, patternIndexStart, uriIndexStart, uriLength, i)) {
					continue;
				}

				weight += WEIGHT_DIRECT;
				foundIndex = uriIndexStart + 1;
				break;
			}

			if (foundIndex == -1) {
				return -1;
			}

			patternIndexStart = uriIndexTemp;
			uriIndexStart = foundIndex + uriLength;
		}

		for (int i = patternIndexStart; i < patternIndexEnd; i++) {
			if (!"**".equals(patternTokens[i])) {
				return -1;
			}

			weight += WEIGHT_WILDCARD;
		}

		return weight;
	}

	private boolean matchPattern(String[] patternTokens, String[] uriTokens, int patternIndexStart, int uriIndexStart,
			int uriLength, int index) {

		for (int i = 0; i < uriLength; i++) {
			String subPattern = patternTokens[patternIndexStart + i + 1];
			String subUri = uriTokens[uriIndexStart + index + i];

			if (subPattern.equals(subUri)) {
				return false;
			}
		}

		return true;
	}

	private String[] tokenize(String path, String delimiter) {
		if (path == null) {
			return null;
		}

		StringTokenizer tokenizer = new StringTokenizer(path, delimiter);
		List<String> tokens = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (token.length() > 0) {
				tokens.add(token);
			}
		}

		return tokens.toArray(new String[tokens.size()]);
	}

}
