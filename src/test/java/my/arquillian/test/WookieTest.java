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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class WookieTest {

	private static final String CONTAINER_A = "Container_A";
	private static final String CONTAINER_B = "Container_B";
	private static final String EAR_A = "EAR_A";
	private static final String EAR_B = "EAR_B";
	private static final String TEST_EAR = "TEST_EAR";
	//private static final String EAR_A_GAV = "my.wookie.project:ear-module-a:ear:?";
	private static final String EAR_A_GAV = "my.wookie.project:ear-module-a:ear:?";
	private static final String EAR_B_GAV = "my.wookie.project:ear-module-b:ear:?";
	
    private static final Logger LOG = LoggerFactory.getLogger(WookieTest.class);    

	@Deployment(name = TEST_EAR, testable = true, order = 1)
	@TargetsContainer(CONTAINER_A)
    public static JavaArchive createDeployment() {
		LOG.info("### Creating test deployment archive");
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(WookieTest.class)
            .addClass(MavenResolver.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	
	@Deployment(name = EAR_A, testable = false, order = 2)
	@TargetsContainer(CONTAINER_A)
	public static EnterpriseArchive createEarA() {
		LOG.info("### Creating EAR A deployment archive");
		return MavenResolver.resolveArchive(EnterpriseArchive.class, EAR_A_GAV);
	}
	
	@Deployment(name = EAR_B, testable = false, order = 2)
	@TargetsContainer(CONTAINER_B)
	public static EnterpriseArchive createEarB() {
		LOG.info("### Creating EAR B deployment archive");
		return MavenResolver.resolveArchive(EnterpriseArchive.class, EAR_B_GAV);
	}
	

	@Test
	@OperateOnDeployment(TEST_EAR)
	public void test_Ejb2x_StatefulA() {
		LOG.info("\n\n ############## Runnign test_Ejb2x_StatefulA #############");
	}
	
	
	
	
	/*    @Deployment(name = TEST_EAR, testable = true, order = 1)
	@TargetsContainer(CONTAINER_A)
	public static EnterpriseArchive createTestDeployment() {
    	EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class);
        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        testJar.addClass(WookieTest.class);
        testJar.addClass(MavenResolver.class);
        final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "Test.war");
        testWar.addAsLibrary(testJar);
        ear.addAsManifestResource("jboss-deployment-structure.xml");
        ear.addAsModule(Testable.archiveToTest(testWar));
		System.out.println(ear.toString(true));
		return ear;
	}*/
}
