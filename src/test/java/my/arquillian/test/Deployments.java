package my.arquillian.test;

import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deployments {

	private static final Logger LOG = LoggerFactory.getLogger(Deployments.class); 

	private static final String EAR_A_GAV = "my.wookie.project:ear-module-a:ear:?";
	private static final String EAR_B_GAV = "my.wookie.project:ear-module-b:ear:?";
	private static final String TEST_API_GAV = "my.wookie.project:test-api:jar:?";
	private static final String REMOTE_API_GAV = "my.wookie.project:remote-api:jar:?";

	private static EnterpriseArchive testEar = null;
	private static EnterpriseArchive earA = null;
	private static EnterpriseArchive earB = null;

	/**
	 * Resolves an artifact from the local maven repository
	 * 
	 * @param archiveFormat
	 * @param coordinates
	 * @return
	 */
	private static <T extends Archive<T>> T resolveArchive(final Class<T> archiveFormat, final String coordinates) {
		LOG.info("### Resolving coordinates [{}]", coordinates);
		return Maven.resolver().offline().loadPomFromFile("pom.xml").resolve(coordinates).withoutTransitivity().asSingle(archiveFormat);
	}

	public static EnterpriseArchive createTestEar(){
		if (testEar == null){
			LOG.info("### Creating test ear ###");
			JavaArchive testApiJar = Deployments.resolveArchive(JavaArchive.class, TEST_API_GAV);
			JavaArchive remoteApiJar = Deployments.resolveArchive(JavaArchive.class, REMOTE_API_GAV);
			EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class);
			ear.addAsLibrary(testApiJar);
			ear.addAsLibrary(remoteApiJar);
			WebArchive testWar = ShrinkWrap.create(WebArchive.class, "Test.war");
			testWar.addClass(PersistenceContextScopeTest.class);
			testWar.addClass(Deployments.class);
			testWar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
			ear.addAsModule(Testable.archiveToTest(testWar));
			testEar = ear;
		}
		return testEar;
	}
	
	public static EnterpriseArchive getEarModuleA(){
		LOG.info("### Getting [ear-module-a] deployment archive ###");
		if (earA == null){
			earA = Deployments.resolveArchive(EnterpriseArchive.class, EAR_A_GAV);
		}
		return earA;
	}
	
	public static EnterpriseArchive getEarModuleB(){
		LOG.info("### Getting [ear-module-b] deployment archive ##");
		if (earB == null){
			earB = Deployments.resolveArchive(EnterpriseArchive.class, EAR_B_GAV);
		}
		return earB;
	}

}
