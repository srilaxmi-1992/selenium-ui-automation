package listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import tests.BaseTest;

import java.io.ByteArrayInputStream;

public class ScreenshotListener implements ITestListener, ISuiteListener {

    private static final Logger log = LogManager.getLogger(ScreenshotListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String screenshotName = "FAILED_" + methodName;

        log.warn("Test FAILED: — attempting screenshot capture", methodName);

        Object testInstance = result.getInstance();
        if (testInstance instanceof BaseTest baseTest) {
            WebDriver driver = baseTest.getDriver();
            if (driver != null) {
                try {
                    byte[] screenshot = ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(
                            screenshotName,
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            "png"
                    );
                    log.warn("Screenshot attached to Allure report as: ", screenshotName);
                } catch (Exception e) {
                    log.error("Failed to capture screenshot for test ", methodName, e.getMessage());
                }
            } else {
                log.error("Driver is null for test  — screenshot skipped", methodName);
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> STARTING test: ", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<<< PASSED  test: ", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("--- SKIPPED test: ", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log.error("TIMED OUT test:", result.getMethod().getMethodName());
        onTestFailure(result);
    }

    @Override
    public void onStart(ISuite suite) {
        log.info("========== SUITE STARTED: {} ==========", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("========== SUITE FINISHED: {} ==========", suite.getName());
    }
}
