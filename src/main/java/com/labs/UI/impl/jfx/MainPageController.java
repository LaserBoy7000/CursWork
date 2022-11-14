package com.labs.UI.impl.jfx;

import java.util.Date;


import java.text.DateFormat;
import java.util.Calendar;

import com.labs.UIAPI.ICommand;
import com.labs.UIAPI.command.constant.GetConstantsCommand;
import com.labs.UIAPI.command.constant.SetConstantCommand;
import com.labs.UIAPI.command.create.CreateExemptionCommand;
import com.labs.UIAPI.command.create.CreateIncomeCommand;
import com.labs.UIAPI.command.create.CreateTaxCommand;
import com.labs.UIAPI.command.identity.LogoutUserCommand;
import com.labs.UIAPI.command.identity.RemoveUserCommand;
import com.labs.UIAPI.command.remove.RemoveCommand;
import com.labs.UIAPI.command.select.SelectByIndexCommand;
import com.labs.UIAPI.command.select.SelectExemptionCommand;
import com.labs.UIAPI.command.select.SelectIncomeCommand;
import com.labs.UIAPI.command.select.SelectTaxCommand;
import com.labs.UIAPI.command.select.SelectUserCommand;
import com.labs.UIAPI.command.select.SelectorConfiguration;
import com.labs.UIAPI.command.update.UpdateCommand;
import com.labs.UIAPI.command.util.GenerateYearlyReportCommand;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.DependenciesInjector;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.ICreator;
import com.labs.core.service.IEssentials;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import com.labs.core.service.IValueProcesssor;
import com.labs.core.service.IWriter;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainPageController {
    private static DependenciesInjector DI;
    private static AsynchronousMachine FL;
    private static java.util.function.Supplier<AsynchronousMachine> Async = () -> MainFX.Flow;
    private static java.util.function.Supplier<DependenciesInjector> Inject = () -> MainFX.DI;

    @FXML private Label Output;

    private boolean DontRemoveSuccessWhenFail = false;
    private Runnable SuccessAction = null;
    private Runnable FailureAction = null;

    public MainPageController(){
        DI = Inject.get();
        FL = Async.get();
        FL.setErrorMessage((String x) -> {
            Platform.runLater(()->{
                printError(x);
                if(FailureAction != null){
                    FailureAction.run();
                    FailureAction = null;
                    if(!DontRemoveSuccessWhenFail)
                        SuccessAction = null;
                }
            });
        });
        FL.setOutputMessage((String x) -> {
            Platform.runLater(()->{
                Output.setTextFill(Color.DARKGREEN);
                Output.setText(x);
                if(SuccessAction != null){
                    SuccessAction.run();
                    SuccessAction = null;
                    FailureAction = null;
                    DontRemoveSuccessWhenFail = false;
                }
            });
        });
        FL.setOutputTable((StringableTable<?> x) -> {
            Platform.runLater(()->SetOutputTable(x));
        });
    }

    public static void setInjectorSuplier(java.util.function.Supplier<DependenciesInjector> injector){
        Inject = injector;
    }

    public static void setAsynchronitySuplier(java.util.function.Supplier<AsynchronousMachine> machine){
        Async = machine;
    }

        //OUTPUT


    private void printError(String string){
        Output.setTextFill(Color.DARKRED);
        Output.setText(string);
    }


        //TABLE


    @FXML private TableView<String[]> Table;

    private ObservableList<String[]> ActiveTable;
    private String CurrentTable = "";
    private boolean AllowsCreate = false;
    private boolean AllowsSelect = true;


    @FXML protected void handleTaxesButtonAction(ActionEvent event){
        setCurentTable("Taxes");
        AllowsCreate = true;
        AllowsSelect = true;
        setCurentProperty("");
        SelectorConfiguration c = new SelectorConfiguration();
        ISelector s = DI.get(ISelector.class);
        c.configure(s);
        SelectTaxCommand cm = new SelectTaxCommand(c);
        cm.setServices(s);
        @SuppressWarnings("unchecked")
        ICommand<StringableTable<?>> cmd = (ICommand<StringableTable<?>>)(ICommand<?>)cm;
        FL.EnqueueTabular(cmd);
    }

    @FXML protected void handleUsersButtonAction(ActionEvent event){
        setCurentTable("Users");
        AllowsCreate = false;
        AllowsSelect = true;
        setCurentProperty("");
        SelectorConfiguration c = new SelectorConfiguration();
        ISelector s = DI.get(ISelector.class);
        c.configure(s);
        SelectUserCommand cm = new SelectUserCommand(c);
        cm.setServices(s);
        @SuppressWarnings("unchecked")
        ICommand<StringableTable<?>> cmd = (ICommand<StringableTable<?>>)(ICommand<?>)cm;
        FL.EnqueueTabular(cmd);
    }

    @FXML protected void handleExemptionsButtonAction(ActionEvent event){
        setCurentTable("Exemptions");
        AllowsCreate = true;
        AllowsSelect = true;
        setCurentProperty("");
        SelectorConfiguration c = new SelectorConfiguration();
        ISelector s = DI.get(ISelector.class);
        c.configure(s);
        SelectExemptionCommand cm = new SelectExemptionCommand(c);
        cm.SetServices(s);
        @SuppressWarnings("unchecked")
        ICommand<StringableTable<?>> cmd = (ICommand<StringableTable<?>>)(ICommand<?>)cm;
        FL.EnqueueTabular(cmd);
    }

    @FXML protected void handleIncomesButtonAction(ActionEvent event){
        setCurentTable("Incomes");
        AllowsCreate = true;
        AllowsSelect = true;
        setCurentProperty("");
        SelectorConfiguration c = new SelectorConfiguration();
        ISelector s = DI.get(ISelector.class);
        c.configure(s);
        SelectIncomeCommand cm = new SelectIncomeCommand(c);
        cm.SetServices(s, DI.get(IIdentityService.class));
        @SuppressWarnings("unchecked")
        ICommand<StringableTable<?>> cmd = (ICommand<StringableTable<?>>)(ICommand<?>)cm;
        FL.EnqueueTabular(cmd);
    }

    @FXML protected void handleConstantsButtonAction(ActionEvent event){
        setCurentTable("Constants");
        AllowsCreate = true;
        AllowsSelect = false;
        setCurentProperty("");
        GetConstantsCommand c = new GetConstantsCommand();
        c.setServices(DI.get(IConstantProvider.class));
        @SuppressWarnings("unchecked")
        ICommand<StringableTable<?>> cmd = (ICommand<StringableTable<?>>)(ICommand<?>)c;
        FL.EnqueueTabularCustom(cmd, (StringableTable<?> x) -> {
            Platform.runLater(()->ShowConstantsTable(x));
        });
    }

    private void setCurentTable(String table){
        CurrentTable = table;
        TabLabel.setText(table);
    }

    private void ShowConstantsTable(StringableTable<?> table){
        EventHandler<CellEditEvent<String[], String>> onUpdate = new EventHandler<CellEditEvent<String[], String>>() {
            @Override
            public void handle(CellEditEvent<String[], String> t) {
                String title = t.getRowValue()[0];
                int y = t.getTablePosition().getRow();
                int x = t.getTablePosition().getColumn();

                if(Creation != null && t.getRowValue().equals(Creation)){
                    Creation[x] = t.getNewValue();
                    return;
                }

                UpdateConstant(x, y, title, t.getNewValue(), t.getOldValue());
            }
        };
        ShowUpdatableTable(table, null, onUpdate, false);
    }

    private void SetOutputTable(StringableTable<?> table){
        EventHandler<CellEditEvent<String[], String>> onUpdate = new EventHandler<CellEditEvent<String[], String>>() {
            @Override
            public void handle(CellEditEvent<String[], String> t) {
                int x = t.getTablePosition().getColumn();

                if(Creation != null && t.getRowValue().equals(Creation)){
                    Creation[x] = t.getNewValue();
                    return;
                }

                int id = Integer.parseInt(t.getRowValue()[0]);
                int y = t.getTablePosition().getRow();

                UpdateValue(id, y, x, t.getTableColumn().getText(), t.getNewValue(), t.getOldValue());
            }
        };

        EventHandler<CellEditEvent<String[], String>> onStart = new EventHandler<CellEditEvent<String[], String>>() {
                @Override
                public void handle(CellEditEvent<String[], String> t) {
                    setCurentProperty(t.getTableColumn().getText());
                }
        };

        ShowUpdatableTable(table, onStart, onUpdate, true);
    }

    private void ShowUpdatableTable(StringableTable<?> table, EventHandler<CellEditEvent<String[], String>> onEditStart, EventHandler<CellEditEvent<String[], String>> onUpdate, boolean lockFirst){
        String[][] tablea = table.getFields();
        String[] heads = table.getHeaders();
        Table.getColumns().clear();

        if(table.HasEntity()){
            Table.setEditable(true);
        } else {
            AllowsCreate = false;
            AllowsSelect = false;
        }

        for (int i = 0; i < heads.length; i++) {
            final int c = i;
            String string  = heads[i];
            TableColumn<String[], String> nc = new TableColumn<String[], String>(string);

            nc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>(){
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> dt){
                    return new SimpleStringProperty(dt.getValue()[c]);
                }
            });

            if(!lockFirst || i!=0){
                nc.setEditable(true);
                nc.setCellFactory(TextFieldTableCell.forTableColumn());
                if(onUpdate != null)
                    nc.setOnEditCommit(onUpdate);
                if(onEditStart != null)
                    nc.setOnEditStart(onEditStart);
            } else nc.setSortable(false);

            Table.getColumns().add(nc);
        }

        ActiveTable = FXCollections.observableArrayList();
        ActiveTable.addAll(tablea);
        Table.setItems(ActiveTable);
    }
    

        //AUTHENTIFICATION


    @FXML private Button LogButt;


    @FXML protected void handleFindButtonAction(ActionEvent event){
        SelectorConfiguration c = new SelectorConfiguration();
        c.FromPrevious = Previous.isSelected();
        c.LexemParam = Current;
        c.Lexem = SearchField.getText();
        makeConfiguredSelection(c);
    }

    @FXML protected void handleLogoutButtonAction(ActionEvent event){
        LogoutUserCommand lo = new LogoutUserCommand();
        lo.setServices(DI.get(IIdentityService.class));
        SuccessAction = ()->setLogin();
        if(CurrentTable.equals("Incomes")){
            ActiveTable.clear();;
            Table.getColumns().clear();
            AllowsCreate = false;
            AllowsSelect = false;
            setCurentTable("");
        }
        FL.Enqueue(lo);
    }

    @FXML protected void handleLoginButtonAction(ActionEvent event){
        Stage w = MainFX.CreateAuthStage();
        w.focusedProperty().addListener((prp, on, nn)->
            {
                if(prp.getValue() == false)
                    w.close();
            });
        DontRemoveSuccessWhenFail = true;
        SuccessAction = ()->{
            w.close();
            setLogout();
        };
        w.show();
    }

    private void setLogout(){
        LogButt.setOnAction(a -> handleLogoutButtonAction(a));
        LogButt.setText("Log out");
    }

    private void setLogin(){
        LogButt.setOnAction(a -> handleLoginButtonAction(a));
        LogButt.setText("Log in");
    }

   
        //FILTERING


    @FXML private CheckBox Previous;
    @FXML private Label PropLabel;
    @FXML private Label PropLabel1;
    @FXML private TextField SearchField;
    @FXML private TextField Min;
    @FXML private TextField Max;
    @FXML private Label TabLabel;

    private String Current = "";


    @FXML protected void handleFilterButtonAction(){
        SelectorConfiguration c = new SelectorConfiguration();
        c.FromPrevious = Previous.isSelected();
        c.MinmaxParam = Current;
        Double mn = null, mx = null;
        if(!Max.getText().isEmpty())
        try{
            String v = Max.getText().replace(',','.');
            mx = Double.parseDouble(v);
        } catch(Exception e){
            printError("WARNING: Filtration parameter must be a number");
            return;
        }
        if(!Min.getText().isEmpty())
        try{
            String v = Min.getText().replace(',','.');
            mn = Double.parseDouble(v);
        } catch(Exception e){
            printError("WARNING: Filtration parameter must be a number");
            return;
        }
        c.Min = mn;
        c.Max = mx;
        makeConfiguredSelection(c);
    }

    @FXML protected void handleReportButtonAction(){
        GenerateYearlyReportCommand gr = new GenerateYearlyReportCommand();
        gr.setServices(DI.get(IIdentityService.class), DI.get(IEssentials.class));
        FL.EnqueueTabular(gr);
    }

    private void makeConfiguredSelection(SelectorConfiguration config){
        if(!AllowsSelect)
            return;

        if(Current.isEmpty()){
            printError("WARNING: No propperty selected");
            return;
        }
        ;
        ISelector sl = DI.get(ISelector.class);
        ICommand<StringableTable<?>> cmd;
        
        switch(CurrentTable){
            case "Users":
                SelectUserCommand su = new SelectUserCommand(config);
                su.setServices(sl);
                @SuppressWarnings("unchecked")
                ICommand<StringableTable<?>> cmd1 = (ICommand<StringableTable<?>>)(ICommand<?>)su;
                cmd = cmd1;
            break;
            case "Incomes":
                SelectIncomeCommand si = new SelectIncomeCommand(config);
                si.SetServices(sl, DI.get(IIdentityService.class));
                @SuppressWarnings("unchecked")
                ICommand<StringableTable<?>> cmd2 = (ICommand<StringableTable<?>>)(ICommand<?>)si;
                cmd = cmd2;
            break;
            case "Exemptions":
                SelectExemptionCommand se = new SelectExemptionCommand(config);
                se.SetServices(sl);
                @SuppressWarnings("unchecked")
                ICommand<StringableTable<?>> cmd3 = (ICommand<StringableTable<?>>)(ICommand<?>)se;
                cmd = cmd3;
            break;
            case "Taxes":
                SelectTaxCommand st = new SelectTaxCommand(config);
                st.setServices(sl);
                @SuppressWarnings("unchecked")
                ICommand<StringableTable<?>> cmd4 = (ICommand<StringableTable<?>>)(ICommand<?>)st;
                cmd = cmd4;
            break;
            default:
                return;
        }
        FL.EnqueueTabular(cmd);
    }

    private void setCurentProperty(String property){
        PropLabel.setText(property);
        PropLabel1.setText(property);
        Current = property;
    }


        //CREATION/REMOVING


    private String[] Creation = null;
   

    @FXML protected void handleAddButtonAction(ActionEvent event){
        if(!AllowsCreate)
            return;
        createCreation();
    }

    @FXML protected void handleRemoveButtonAction(ActionEvent event){
        String[] item = Table.getSelectionModel().getSelectedItem();
        if(Creation != null && Creation.equals(item)){
            cancelCreation();
            return;
        }
        if(CurrentTable.equals("Constants"))
            return;

        SelectByIndexCommand cm = new SelectByIndexCommand(Integer.parseInt(item[0])-1);
        cm.setServices(DI.get(ISelector.class));
        cm.execute();

        SuccessAction = () -> ActiveTable.remove(item);

        if(CurrentTable.equals("Users")){
            removeUser(item[1], item[2]);
            return;
        }

        RemoveCommand cmd = new RemoveCommand(false);
        cmd.setServices(DI.get(IWriter.class));

        FL.Enqueue(cmd);
    }

    @FXML protected void handleOkButtonAction(ActionEvent event){
        if(Creation == null)
            return;

        if(CurrentTable.equals("Constants"))
            CreateConstant();
        else CreateEntity();
    }

    private void removeUser(String Name, String Surename){
        RemoveUserCommand rs = new RemoveUserCommand(Name, Surename);
        rs.setServices(DI.get(IIdentityService.class));
        FL.Enqueue(rs);
    }

    private void cancelCreation(){
        ActiveTable.remove(Creation);
        Creation = null;
    }

    private void createCreation(){
        if(Creation != null)
            cancelCreation();
        Creation = genTemplate(Table.getColumns().size());
        ActiveTable.add(Creation);
    }

    private void CreateConstant(){
        double v = Double.NaN;
        try {
            String s = new String(Creation[1]);
            s = s.replace(',', '.');
            v = Double.parseDouble(s);
        } catch (Exception e) {
            printError("WARNING: Invalid value");
            return;
        }
        SetConstantCommand st = new SetConstantCommand(Creation[0], v);
        st.setServices(DI.get(IConstantProvider.class));
        SuccessAction = () -> Creation = null;
        FL.Enqueue(st);
    }

    private void CreateEntity(){
        switch(CurrentTable){
            case "Exemptions":
                CreateExemptionCommand ce = new CreateExemptionCommand(Creation[0], Creation[1]);
                ce.setServices(DI.get(ICreator.class));
                SuccessAction = () -> cancelCreation();
                FL.Enqueue(ce);
            break;
            case "Taxes":
                CreateTax();
            break;
            case "Incomes":
                CreateIncome();
            break;
        }
    }

    private void CreateTax(){
        double tp;
        double wc;
        boolean plg;
        try{
            tp = Double.parseDouble(Creation[2].replace(',', '.'));
        } catch (Exception e) {
            printError("ERROR: TIPP must be a number");
            return;
        }
        try{
            wc = Double.parseDouble(Creation[3].replace(',', '.'));
        } catch (Exception e) {
            printError("ERROR: WC must be a number");
            return;
        }
        IValueProcesssor p = DI.get(IValueProcesssor.class);
        Object v = null;
        v = p.convert("AllowsExemption", Creation[4]);
        if(p.getType() != boolean.class){
            printError("ERROR: AllowsExemption must be a bool");
            return;
        }
        plg = (boolean)v;
        
        CreateTaxCommand c = new CreateTaxCommand(Creation[1], Creation[5], plg, tp, wc);
        c.setServices(DI.get(ICreator.class));
        SuccessAction = () -> cancelCreation();
        FL.Enqueue(c);
    }

    private void CreateIncome(){
        double sm;
        Date dt;
        try{
            sm = Double.parseDouble(Creation[3].replace(',', '.'));
        } catch (Exception e) {
            printError("ERROR: Income value must be a number");
            return;
        }
        try{
            dt = DateFormat.getInstance().parse(Creation[2]);
        } catch (Exception e) { 
            dt = Calendar.getInstance().getTime();
        }
        CreateIncomeCommand c = new CreateIncomeCommand(Creation[1], sm, dt);
        c.setServices(DI.get(ICreator.class), DI.get(IIdentityService.class));
        SuccessAction = () -> cancelCreation();
        FL.Enqueue(c);
    }


        //UPDATING
   

    private String[] genTemplate(int columns){
        String[] rs = new String[columns];
        for(int i = 0; i < columns; i++){
            rs[i] = "new_value";
        }
        return rs;
    }

    private void UpdateValue(int idx, int y, int x, String property, String value, String oldvalue){
        SelectByIndexCommand cm = new SelectByIndexCommand(idx-1);
        cm.setServices(DI.get(ISelector.class));
        cm.execute();

        IValueProcesssor p = DI.get(IValueProcesssor.class);
        Object v = null;
        if(value.equals("<empty>"))
            value = null;
        v = p.convert(property, value);
        if(!p.isSucceed()){
            printError("WARNING: Failed to find value " + value + " for " + property);
            ActiveTable.set(y,ActiveTable.get(y));
            return;
        }

        UpdateCommand upd = new UpdateCommand(property, v, p.getType());
        upd.setServices(DI.get(IWriter.class));

        FailureAction = () -> ActiveTable.set(y,ActiveTable.get(y));

        final String f = value;

        SuccessAction = () -> ActiveTable.get(y)[x] = f;

        FL.Enqueue(upd);
    }

    private void UpdateConstant(int x, int y, String value, String newValue, String oldValue){
        if(x == 0){
            printError("WARNING: Constant name cannot be updated");
            ActiveTable.set(y,ActiveTable.get(y));
            return;
        }

        double v = Double.NaN;
        try {
            newValue = newValue.replace(',', '.');
            v = Double.parseDouble(newValue);
        } catch (Exception e) {
            printError("WARNING: Invalid value");
            ActiveTable.get(y)[x] = oldValue;
            ActiveTable.set(y,ActiveTable.get(y));
            return;
        }

        SetConstantCommand c = new SetConstantCommand(value, v);
        c.setServices(DI.get(IConstantProvider.class));

        FailureAction = () -> ActiveTable.set(y,ActiveTable.get(y));

        String f = String.format("%.2f", v);

        SuccessAction = () -> ActiveTable.get(y)[x] = f;

        FL.Enqueue(c);
    }
}
