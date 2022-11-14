package com.labs.jfx;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.labs.UI.impl.jfx.AsynchronousMachine;
import com.labs.UI.impl.jfx.MainFX;
import com.labs.UI.impl.jfx.MainPageController;
import com.labs.core.service.DependenciesInjector;
import com.labs.testingutils.TestConfigModule;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainTest extends ApplicationTest {

    private static final String MAIN_PAGE = "layout/main.fxml";

    @Override
    public void start(Stage stage) {
        Parent p = null;
        MainPageController.setAsynchronitySuplier(()->new AsynchronousMachine());
        MainPageController.setInjectorSuplier(()->new DependenciesInjector(new TestConfigModule()));
        try {
            p = FXMLLoader.load(MainFX.class.getResource(MAIN_PAGE));
        } catch (IOException e) { e.printStackTrace(); }
        stage.setScene(new Scene(p));
        stage.show();
    }

    @Test
    public void testIncomesLock() throws InterruptedException{
        Label out = lookup("#Output").query();
        assertTrue(!out.getText().contains("WARNING:"));
        Button inc = lookup("#IncomeButton").query();
        clickOn(inc);
        while(out.getText().isEmpty())
            Thread.sleep(10);
        assertTrue(out.getText().contains("WARNING:"));
    }

    @Test
    public void testTaxResult() throws InterruptedException{
        TableView<String[]> tb = lookup("#Table").query();
        Label out = lookup("#Output").query();
        assertTrue(tb.getColumns().size()==0);
        Button tx = lookup("#TaxButton").query();
        clickOn(tx);
        while(out.getText().isEmpty())
            Thread.sleep(10);
        assertTrue(out.getTextFill().equals((Paint)Color.DARKGREEN));
        while(tb.getColumns().size() < 2)
            Thread.sleep(10);
        assertTrue(tb.getColumns().get(1).getText().equals("Title"));
    }
    
}
