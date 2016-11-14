package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.core.sample.ExtensionSampleOne;
import com.paypal.butterfly.core.sample.SampleTransformationTemplate;
import com.paypal.butterfly.core.sample.SampleUpgradeStep;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * ButteflyFacadeImplTest
 *
 * Created by vkuncham on 11/7/2016.
 */
public class ButteflyFacadeImplTest extends PowerMockTestCase {

    @InjectMocks
    private ButterflyFacadeImpl butterflyFacadeImpl;

    @Mock
    private ExtensionRegistry extensionRegistry;

    @Mock
    private TransformationEngine transformationEngine;

    private ExtensionRegistry extensionRegistry_test = new ExtensionRegistry();

    private File applicationFolder = new File(this.getClass().getClassLoader().getResource("testTransformation").getFile());

    @Test
    public void testGetRegisteredExtensions() {
        when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
        List<Extension> list = butterflyFacadeImpl.getRegisteredExtensions();
        Extension extension = list.get(0);
        Assert.assertEquals(list.size(),2);
        Assert.assertTrue(extension instanceof ExtensionSampleOne);
    }

    @Test
    public void testAutomaticResolutionAsNull() throws TemplateResolutionException {
      when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
      Assert.assertEquals(butterflyFacadeImpl.automaticResolution(new File("testTransformation1")),null);
    }

    @Test
    public void testAutomaticResolutionAsNotNull() throws TemplateResolutionException {
      when(extensionRegistry.getExtensions()).thenReturn(extensionRegistry_test.getExtensions());
      Assert.assertEquals(butterflyFacadeImpl.automaticResolution(applicationFolder),SampleTransformationTemplate.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Template class name cannot be blank")
    public void testTransformWithTemplateClassAsEmptyString() throws ButterflyException {
       butterflyFacadeImpl.transform(applicationFolder,"");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Template class name cannot be blank")
    public void testTransformWithTemplateAsNull() throws ButterflyException {
        butterflyFacadeImpl.transform(applicationFolder,(String) null);
    }


    @Test(expectedExceptions = InternalException.class,
            expectedExceptionsMessageRegExp = "Template class TestTemplate not found.*")
    public void testTransformWithInValidTemplate() throws ButterflyException {
      butterflyFacadeImpl.transform(applicationFolder,"TestTemplate");
    }

    @Test
    public void testTransformWithValidTemplate() throws ButterflyException {

        TemplateTransformation  templateTransformation = new TemplateTransformation(new Application(applicationFolder),
                new SampleTransformationTemplate(),new Configuration());
        butterflyFacadeImpl.transform(applicationFolder, "com.paypal.butterfly.core.sample.SampleTransformationTemplate");
        verify(transformationEngine,times(1)).perform((TemplateTransformation) anyObject());
    }


    @Test(expectedExceptions = InternalException.class,
            expectedExceptionsMessageRegExp = "Template class class com.paypal.butterfly.core.sample." +
                    "SampleAbstractTransformationTemplate could not be instantiated.*")
    public void testTransformWithAbstractTemplate() throws ButterflyException {
         butterflyFacadeImpl.transform(applicationFolder,"com.paypal.butterfly.core.sample.SampleAbstractTransformationTemplate");
    }

    @Test
    public void testTransformWithValidTemplateAsClass() throws ButterflyException {
         butterflyFacadeImpl.transform(applicationFolder,SampleTransformationTemplate.class);
        verify(transformationEngine,times(1)).perform((TemplateTransformation) anyObject());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp ="Invalid application folder testTransformation1"
    )
    public void testTransformWithValidUpgradePathInvalidAppFolder() throws ButterflyException {
        UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
        butterflyFacadeImpl.transform(new File("testTransformation1"),upgradePath);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp ="Upgrade path cannot be null"
    )
    public void testTransformWithInValidUpgradePath() throws ButterflyException {
          butterflyFacadeImpl.transform(applicationFolder, (UpgradePath) null);
    }

    @Test
    public void testTransformWithValidUpgradePath() throws ButterflyException {
        UpgradePath  upgradePath = new UpgradePath(SampleUpgradeStep.class);
        butterflyFacadeImpl.transform(applicationFolder,upgradePath);
        verify(transformationEngine,times(1)).perform((UpgradePathTransformation)anyObject());
    }
}
