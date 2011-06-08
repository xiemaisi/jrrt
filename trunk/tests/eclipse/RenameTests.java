package tests.eclipse;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RenameTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(RenameTests.class.getName());
		suite.addTestSuite(tests.eclipse.RenameMethodInInterface.RenameMethodInInterfaceTests.class);
		suite.addTestSuite(tests.eclipse.RenameNonPrivateField.RenameNonPrivateFieldTests.class);
		suite.addTestSuite(tests.eclipse.RenamePackage.RenamePackageTests.class);
		suite.addTestSuite(tests.eclipse.RenameParameters.RenameParametersTests.class);
		suite.addTestSuite(tests.eclipse.RenamePrivateField.RenamePrivateFieldTests.class);
		suite.addTestSuite(tests.eclipse.RenamePrivateMethod.RenamePrivateMethodTests.class);
		suite.addTestSuite(tests.eclipse.RenameStaticMethod.RenameStaticMethodTests.class);
		suite.addTestSuite(tests.eclipse.RenameTemp.RenameTempTests.class);
		suite.addTestSuite(tests.eclipse.RenameType.RenameTypeTests.class);
		suite.addTestSuite(tests.eclipse.RenameTypeParameter.RenameTypeParameterTests.class);
		suite.addTestSuite(tests.eclipse.RenameVirtualMethodInClass.RenameVirtualMethodInClassTests.class);
		return suite;
	}

}
