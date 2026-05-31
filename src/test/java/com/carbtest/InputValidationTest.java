package com.carbtest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class InputValidationTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;

        driver.get("https://www.calculator.net/carbohydrate-calculator.html");

        // Click US Units tab to ensure the correct fields are shown
        driver.findElement(By.xpath("//a[contains(@onclick,\"'standard'\")]")).click();

        // Wait for Age field to be visible before proceeding
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cage")));
    }

    // ─────────────────────────────────────────────────────────────
    // TC-29: Float value in Weight field
    // Expected: result table shown, no NaN
    // ─────────────────────────────────────────────────────────────
    @Test
    void tc29_floatValueInWeightField() {
        // Age
        js.executeScript("document.getElementById('cage').value = '30'");

        // Gender - Male (click the label since the input is hidden behind a styled span)
        driver.findElement(By.cssSelector("label[for='csex1']")).click();

        // Height - using JavaScript to set values since fields may not be interactable directly
        js.executeScript("document.getElementById('cheightfeet').value = '5'");
        js.executeScript("document.getElementById('cheightinch').value = '7'");

        // Weight - float value
        js.executeScript("document.getElementById('cpound').value = '150.75'");

        // Activity
        new Select(driver.findElement(By.id("cactivity")))
            .selectByVisibleText("Moderate: exercise 4-5 times/week");

        // Calculate
        driver.findElement(By.name("x")).click();

        // Wait for result table and assert it is displayed with no NaN
        WebElement result = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.cinfoT"))
        );
        Assertions.assertTrue(result.isDisplayed(),
            "Result table should display for float weight input");
        Assertions.assertFalse(result.getText().contains("NaN"),
            "Result should not contain NaN");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-30: Float value in Height inches field
    // Expected: result table shown, no NaN
    // ─────────────────────────────────────────────────────────────
    @Test
    void tc30_floatValueInHeightInchesField() {
        // Age
        js.executeScript("document.getElementById('cage').value = '30'");

        // Gender - Male
        driver.findElement(By.cssSelector("label[for='csex1']")).click();

        // Height - float inches value
        js.executeScript("document.getElementById('cheightfeet').value = '5'");
        js.executeScript("document.getElementById('cheightinch').value = '7.5'");

        // Weight
        js.executeScript("document.getElementById('cpound').value = '150'");

        // Activity
        new Select(driver.findElement(By.id("cactivity")))
            .selectByVisibleText("Moderate: exercise 4-5 times/week");

        // Calculate
        driver.findElement(By.name("x")).click();

        // Wait for result table and assert it is displayed with no NaN
        WebElement result = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.cinfoT"))
        );
        Assertions.assertTrue(result.isDisplayed(),
            "Result table should display for float height input");
        Assertions.assertFalse(result.getText().contains("NaN"),
            "Result should not contain NaN");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-31: Empty Age field
    // Expected: error message shown
    // ─────────────────────────────────────────────────────────────
    @Test
    void tc31_emptyAgeField() {
        // Age - cleared to empty (default value is 25 so must be explicitly cleared)
        js.executeScript("document.getElementById('cage').value = ''");

        // Gender - Male
        driver.findElement(By.cssSelector("label[for='csex1']")).click();

        // Height
        js.executeScript("document.getElementById('cheightfeet').value = '5'");
        js.executeScript("document.getElementById('cheightinch').value = '7'");

        // Weight
        js.executeScript("document.getElementById('cpound').value = '150'");

        // Activity
        new Select(driver.findElement(By.id("cactivity")))
            .selectByVisibleText("Moderate: exercise 4-5 times/week");

        // Calculate
        driver.findElement(By.name("x")).click();

        // Wait for error message and assert it contains the expected text
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("font[color='red']"))
        );
        Assertions.assertTrue(error.isDisplayed(),
            "Error message should appear when Age is empty");
        Assertions.assertTrue(error.getText().contains("Please provide an age between 18 and 80"),
            "Error text should mention valid age range");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-32: Empty Height field
    // Expected: error message shown
    // ─────────────────────────────────────────────────────────────
    @Test
    void tc32_emptyHeightField() {
        // Age
        js.executeScript("document.getElementById('cage').value = '30'");

        // Gender - Male
        driver.findElement(By.cssSelector("label[for='csex1']")).click();

        // Height - both fields cleared to empty (defaults are 5 and 10)
        js.executeScript("document.getElementById('cheightfeet').value = ''");
        js.executeScript("document.getElementById('cheightinch').value = ''");

        // Weight
        js.executeScript("document.getElementById('cpound').value = '150'");

        // Activity
        new Select(driver.findElement(By.id("cactivity")))
            .selectByVisibleText("Moderate: exercise 4-5 times/week");

        // Calculate
        driver.findElement(By.name("x")).click();

        // Wait for error message and assert it is displayed
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("font[color='red']"))
        );
        Assertions.assertTrue(error.isDisplayed(),
            "Error message should appear when Height is empty");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}