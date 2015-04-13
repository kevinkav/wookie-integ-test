/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package my.arquillian.test;

import static org.junit.Assert.fail;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.naming.NamingException;

import my.remote.bean.locator.Ejb3xBeanLocator;
import my.test.api.TestCaseRemote;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class PersistenceContextScopeTest {

	private static final String CONTAINER_1_NON_JTS = "Container_1_non_jts";
	private static final String CONTAINER_2_NON_JTS = "Container_2_non_jts";
	private static final String CONTAINER_3_JTS = "Container_3_jts";
	private static final String CONTAINER_4_JTS = "Container_4_jts";
	private static final int CONTAINER_1_OFFSET = 0; 
	private static final int CONTAINER_2_OFFSET = 100;
	private static final int CONTAINER_3_OFFSET = 200;
	private static final int CONTAINER_4_OFFSET = 300;
	private static final String EAR_A = "EAR_A";
	private static final String EAR_B = "EAR_B";
	private static final String TEST = "Test";
	private static final String PASSED = "Passed";
	private static final String FAILED = "Failed";
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceContextScopeTest.class);    
  
    @Inject
    Ejb3xBeanLocator ejb3xBeanLocator;
       
    
    
    /* **************************************************************
     *                Deployments Containers 1 & 2
     **************************************************************** */
    
	@Deployment(name = TEST + CONTAINER_1_NON_JTS, testable = true, order = 1)
	@TargetsContainer(CONTAINER_1_NON_JTS)
    public static EnterpriseArchive createTestEar_Container1() {
		return Deployments.createTestEar();
    }
	
	@Deployment(name = EAR_A + CONTAINER_1_NON_JTS, testable = false, order = 2)
	@TargetsContainer(CONTAINER_1_NON_JTS)
	public static EnterpriseArchive getEarA_Container1() {
		return Deployments.getEarModuleA();
	}
	
	@Deployment(name = EAR_B + CONTAINER_2_NON_JTS, testable = false, order = 3)
	@TargetsContainer(CONTAINER_2_NON_JTS)
	public static EnterpriseArchive getEarB_Container2() {
		return Deployments.getEarModuleB();
	}
    
    /* **************************************************************
     *                Deployments Containers 3 & 4
     **************************************************************** */
    
	@Deployment(name = TEST + CONTAINER_3_JTS, testable = true, order = 4)
	@TargetsContainer(CONTAINER_3_JTS)
    public static EnterpriseArchive createTestEar() {
		return Deployments.createTestEar();
    }
	
	@Deployment(name = EAR_A + CONTAINER_3_JTS, testable = false, order = 5)
	@TargetsContainer(CONTAINER_3_JTS)
	public static EnterpriseArchive getEarA() {
		return Deployments.getEarModuleA();
	}
	
	@Deployment(name = EAR_B + CONTAINER_4_JTS, testable = false, order = 6)
	@TargetsContainer(CONTAINER_4_JTS)
	public static EnterpriseArchive getEarB() {
		return Deployments.getEarModuleB();
	}
	
	/* ***********************************************
	 *                Test Cases
	 ************************************************* */
	
	@Test
	@OperateOnDeployment(TEST + CONTAINER_1_NON_JTS)
	public void test_Ejb3x_StatelessA_Without_Jts() throws Exception {
		LOG.info("### Beginning test [1] ###");
		String lookup = "ejb:ear-module-a-1.0-SNAPSHOT/ejb-module-a-1.0-SNAPSHOT/Ejb3x_StatelessA!my.test.api.TestCaseRemote";
		TestCaseRemote testCase = (TestCaseRemote)ejb3xBeanLocator.locateBean(lookup);
		Assert.assertNotNull(testCase);
		testCase.setUp();
		try {
			testCase.runTest(CONTAINER_1_OFFSET, CONTAINER_2_OFFSET);
			fail("Should have got EJBTransactionRolledbackException here!");
		}catch (EJBTransactionRolledbackException ex){
			LOG.info("### Received expected exception ###");
		}finally{
			testCase.tearDown();
		}
		LOG.info("### Finished test [1] ###");
	}
	
	
	@Test
	@OperateOnDeployment(TEST + CONTAINER_1_NON_JTS)
	public void test_Ejb3x_StatefulA_Without_Jts() throws Exception {
		LOG.info("### Beginning test [2] ###");
		String lookup = "ejb:ear-module-a-1.0-SNAPSHOT/ejb-module-a-1.0-SNAPSHOT/Ejb3x_StatefulA!my.test.api.TestCaseRemote?stateful";
		TestCaseRemote testCase = (TestCaseRemote)ejb3xBeanLocator.locateBean(lookup);
		Assert.assertNotNull(testCase);
		testCase.setUp();
		try {
			testCase.runTest(CONTAINER_1_OFFSET, CONTAINER_2_OFFSET);
			fail("Should have got EJBTransactionRolledbackException here!");
		}catch (NamingException ex){
			LOG.info("### Received expected exception ###");
		}finally{
			testCase.tearDown();
		}
		LOG.info("### Finished test [2] ###");
	}

	@Test
	@OperateOnDeployment(TEST + CONTAINER_3_JTS)
	public void test_Ejb2x_StatelessA_With_Jts() throws Exception {
		LOG.info("### Beginning test [3] ###");
		String lookup = "ejb:ear-module-a-1.0-SNAPSHOT/ejb-module-a-1.0-SNAPSHOT/Ejb2x_StatelessA!my.test.api.TestCaseRemote";
		TestCaseRemote testCase = (TestCaseRemote)ejb3xBeanLocator.locateBean(lookup);
		Assert.assertNotNull(testCase);
		testCase.setUp();
		testCase.runTest(CONTAINER_3_OFFSET, CONTAINER_4_OFFSET);
		testCase.tearDown();
		LOG.info("### Finished test [3] ###");
	}
	
	
	@Test
	@OperateOnDeployment(TEST + CONTAINER_3_JTS)
	public void test_Ejb2x_StatefulA_With_Jts() throws Exception {
		LOG.info("### Beginning test [4] ###");
		String lookup = "ejb:ear-module-a-1.0-SNAPSHOT/ejb-module-a-1.0-SNAPSHOT/Ejb2x_StatefulA!my.test.api.TestCaseRemote?stateful";
		TestCaseRemote testCase = (TestCaseRemote)ejb3xBeanLocator.locateBean(lookup);
		Assert.assertNotNull(testCase);
		testCase.setUp();
		testCase.runTest(CONTAINER_3_OFFSET, CONTAINER_4_OFFSET);
		testCase.tearDown();
		LOG.info("### Finished test [4] ###");
	}
	


	
	
}
