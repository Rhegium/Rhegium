package org.rhegium.internal.validator;

import static org.junit.Assert.fail;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.rhegium.api.validator.Validate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ValidationTestCase {

	@Test(expected = ValidationException.class)
	public void testValidationSettermethodLevelFail() throws Exception {
		Injector injector = buildInjector();
		SetterMethodLevelTest instance = injector.getInstance(SetterMethodLevelTest.class);

		try {
			instance.setTest1(null);
		}
		catch (ConstraintViolationException e) {
			fail("setTest1(null) thrown ConstraintViolationException");
		}
		finally {
			try {
				instance.setTest2(null);
			}
			catch (ValidationException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	@Test(expected = ValidationException.class)
	public void testValidationSetterFail() throws Exception {
		Injector injector = buildInjector();
		SetterTest instance = injector.getInstance(SetterTest.class);
		try {
			instance.setTest(null);
		}
		catch (ValidationException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testValidationSetterSucceed() throws Exception {
		Injector injector = buildInjector();
		SetterTest instance = injector.getInstance(SetterTest.class);
		instance.setTest("test");
	}

	@Test(expected = ValidationException.class)
	public void testValidationGetterFail() throws Exception {
		Injector injector = buildInjector();
		GetterTestFail instance = injector.getInstance(GetterTestFail.class);
		try {
			instance.getTestString();
		}
		catch (ValidationException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testValidationGetterSucceed() throws Exception {
		Injector injector = buildInjector();
		GetterTestSucceed instance = injector.getInstance(GetterTestSucceed.class);
		instance.getTestString();
	}

	private Injector buildInjector() {
		return Guice.createInjector(new Module());
	}

	public static class Module extends AbstractModule {

		@Override
		protected void configure() {
			install(new ValidationModule());

			bind(GetterTestFail.class);
			bind(GetterTestSucceed.class);
			bind(SetterTest.class);
			bind(SetterMethodLevelTest.class);
		}
	}

	@Validate
	public static class GetterTestFail {

		private final String testString = null;

		@NotNull
		public String getTestString() {
			return testString;
		}

	}

	@Validate
	public static class GetterTestSucceed {

		private final String testString = "test";

		@NotNull
		public String getTestString() {
			return testString;
		}

	}

	@Validate
	public static class SetterTest {

		public void setTest(@NotNull String test) {
		}

	}

	public static class SetterMethodLevelTest {

		public void setTest1(@NotNull String test) {
		}

		@Validate
		public void setTest2(@NotNull String test) {
		}

	}

}
