package com.labs.UI.impl.СonsoleUI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.labs.UI.impl.СonsoleUI.Platform.Customization;
import com.labs.UI.impl.СonsoleUI.Platform.Customization.BackgroundColors;
import com.labs.UIAPI.*;
import com.labs.UIAPI.command.constant.*;
import com.labs.UIAPI.command.create.*;
import com.labs.UIAPI.command.identity.*;
import com.labs.UIAPI.command.remove.RemoveCommand;
import com.labs.UIAPI.command.select.*;
import com.labs.UIAPI.command.update.UpdateCommand;
import com.labs.UIAPI.command.util.GenerateYearlyReportCommand;
import com.labs.core.diagnostics.LogLevel;
import com.labs.core.diagnostics.Logger;
import com.labs.core.service.DependenciesInjector;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.ICreator;
import com.labs.core.service.IEssentials;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import com.labs.core.service.IValueProcesssor;
import com.labs.core.service.IWriter;
import com.labs.UI.IUserInterface;

public class ConsoleUI implements IUserInterface {

    private String curentPrefix = "@-no user-@=> ";
    private final Customization.FontColors PREFIX_COLOR = Customization.FontColors.Green;
    private final Customization.FontColors WARNING_COLOR = Customization.FontColors.Yellow;
    private final Customization.FontColors FATAL_COLOR = Customization.FontColors.Red;
    private List<Thread> ThreadPool = new ArrayList<>();
    private DependenciesInjector DI;

    public ConsoleUI(DependenciesInjector DI){
        this.DI = DI;
    }

    public void Init() 
    {
        HiMessage();
        MessageLoop();
    }

    void MessageLoop()
    {
        while(true)
        {
            String line = Read();
            line = line.trim();
            String[] params = line.split(" +");

            if(params.length == 0) continue;

            switch(params[0])
            {
                case "Select":
                    Select(params);
                    break;

                case "Create":
                    Create(params);
                    break;

                case "Update":
                    Update(params);
                    break;

                case "Login":
                    Login(params);
                    break;

                case "Logout":
                    Logout(params);
                    break;

                case "Constant":
                    Constant(params);
                    break;

                case "Remove":
                    Remove(params);
                    break;

                case "ALL":
                    GenYearlyReport();
                    break;

                case "exit":
                    KillAll();
                    System.exit(0);
                    break;

                default:
                    PrintError("WARNING: Invalid command", false);
                    break;
            }
        }
    }

    //IO TEXT
    
    void HiMessage()
    {
        Print("*** Вітаю у світі бухгалтерії, друже! :D ***", false);
    }

    void Print(String text, boolean newLine)
    {
        if(newLine){
            System.out.println("");
            PrintPrefix();
            System.out.println(text);
            PrintPrefix();
        } else {
            PrintPrefix();
            System.out.println(text);
        }
    }

    void PrintPrefix(){
        RequestPrefix();
        Customization.INSTANCE.SetFontColor(PREFIX_COLOR, BackgroundColors.None);
        System.out.print(curentPrefix);
        Customization.INSTANCE.SetFontDefault();
    }

    String Read()
    {
        PrintPrefix();
        return System.console().readLine();
    }

    void PrintError(String message, boolean newLine)
    {
        RequestPrefix();
        if(message.contains("WARNING"))
            Customization.INSTANCE.SetFontColor(WARNING_COLOR, BackgroundColors.None);
        else
            Customization.INSTANCE.SetFontColor(FATAL_COLOR, BackgroundColors.None);
        if(!newLine){
            System.out.print(curentPrefix);
            System.out.println(message);
            return;
        }
        System.out.print("\n"+curentPrefix);
        System.out.println(message);
        Customization.INSTANCE.SetFontColor(PREFIX_COLOR, BackgroundColors.None);
        System.out.print(curentPrefix);
        Customization.INSTANCE.SetFontDefault();

    }

    void RequestPrefix()
    {
        IIdentityService id = DI.get(IIdentityService.class);
        if(!id.isUserAvailable())
            curentPrefix = curentPrefix.replaceFirst("-.*-", "-no user-");
        else curentPrefix = curentPrefix.replaceFirst("-.*-", "-"+id.getCurrentUser().getName()+"-");
    }

    //INTERNAL COMMAND HANDLERS

        //SELECT

    void Select(String[] context)
    {
        if(context.length < 2){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        switch(context[1]){
            case "Tax":
                SelectTax(context);
                break;
            case "Exemption":
                SelectExemption(context);
                break;
            case "Income":
                SelectIncome(context);
                break;
            case "User":
                SelectUser(context);
                break;
            default:
                PrintError("WARNING: Invalid command", false);
                break;
        }
    }

    void SelectTax(String[] context){
        String paramspace = "";
        for(int i = 2; i < context.length; i++)
            paramspace += " " +   context[i];

        SelectorConfiguration cf = buildSelectorConfig(paramspace);
        if(cf == null)
            return;
        SelectTaxCommand c = new SelectTaxCommand(cf);
        c.setServices(DI.get(ISelector.class));
        Enqueue(c);
    }

    void SelectIncome(String[] context){
        String paramspace = "";
        for(int i = 2; i < context.length; i++)
            paramspace += " " +  context[i];

        SelectorConfiguration cf = buildSelectorConfig(paramspace);
        if(cf == null)
            return;
        SelectIncomeCommand c = new SelectIncomeCommand(cf);
        c.SetServices(DI.get(ISelector.class), DI.get(IIdentityService.class));;
        Enqueue(c);
    }

    void SelectUser(String[] context){
        String paramspace = "";
        for(int i = 2; i < context.length; i++)
            paramspace += " " +  context[i];

        SelectorConfiguration cf = buildSelectorConfig(paramspace);
        if(cf == null)
            return;
        SelectUserCommand c = new SelectUserCommand(cf);
        c.setServices(DI.get(ISelector.class));
        Enqueue(c);
    }

    void SelectExemption(String[] context){
        String paramspace = "";
        for(int i = 2; i < context.length; i++)
            paramspace +=  " " + context[i];

        SelectorConfiguration cf = buildSelectorConfig(paramspace);
        if(cf == null)
            return;
        SelectExemptionCommand c = new SelectExemptionCommand(cf);
        c.SetServices(DI.get(ISelector.class));
        Enqueue(c);
    }

    private SelectorConfiguration buildSelectorConfig(String paramSpace){
        SelectorConfiguration c = new SelectorConfiguration();
        Pattern p = Pattern.compile ("@([A-z]+)(?: +([^ @]+))?(?: +([^ @]+))?");
        Matcher m = p.matcher(paramSpace);
        while(m.find()){
            switch(m.group(1)){
                case "st":
                    if(m.group(2) != null){
                        Double v = null;
                        try{
                            v = Double.parseDouble(m.group(2));
                        } catch (Exception e) {
                            PrintError("WARNING: Invalid value parameter for @st", false);
                            return null;
                        }

                        if(m.group(3) != null){
                            c.Min = v;
                            c.MinmaxParam = m.group(3);
                            break;
                        } else PrintError("WARNING: Invalid name parameter for @st", false);
                        return null;
                    }
                    PrintError("WARNING: Invalid parameter for @st", false);
                    return null;
                case "ed":
                    if(m.group(2) != null){
                        Double v = null;
                        try{
                            v = Double.parseDouble(m.group(2));
                        } catch (Exception e) {
                            PrintError("WARNING: Invalid value parameter for @st", false);
                            return null;
                        }

                        if(m.group(3) != null){
                            c.Max = v;
                            c.MinmaxParam = m.group(3);
                            break;
                        } else PrintError("WARNING: Invalid name parameter for @st", false);
                        return null;
                    }
                    PrintError("WARNING: Invalid parameters for @ed", false);
                    return null;
                case "lk":
                    if(m.group(2) != null){
                        if(m.group(3) != null){
                            c.Lexem = m.group(2);
                            c.LexemParam = m.group(3);
                            break;
                        } else PrintError("WARNING: Invalid name parameter for @lk", false);
                        return null;
                    }
                    PrintError("WARNING: Invalid parameters for @lk", false);
                    return null;
                
                case "sr":
                    String g = m.group(2);
                    String n = m.group(3);
                    if(n != null){
                        if(g == null){
                            PrintError("WARNING: Invalid value parameter for @sr", false);
                            return null;
                        }
                        if(g.equals("d"))
                            c.Desc = true;
                        else c.Desc = false;
                        c.SortParam = n;
                        break;
                    }
                    PrintError("WARNING: Invalid name parameter for @sr", false);
                    return null;
                
                case "lst":
                    c.FromPrevious = true;
                    break;

                case "sg":
                    String gs = m.group(2);
                    if(gs != null){
                        try{
                            c.SelectN = Integer.parseInt(gs);
                            break;
                        } catch (Exception e){
                            PrintError("WARNING: Invalid value parameter for @sg", false);
                            return null;
                        }
                    }
                    PrintError("WARNING: Invalid value parameter for @sg", false);
                    return null;
                default:
                    PrintError("WARNING: Inrecognized selector parameter", false);
                    return null;
            }
        }
        return c;
    }

        //CREATE

    void Create(String[] context){
        if(context.length < 2){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        switch(context[1]){
            case "Tax":
                CreateTax(context);
                break;
            case "Exemption":
                CreateExemption(context);
                break;
            case "Income":
                CreateIncome(context);
                break;
            default:
                PrintError("WARNING: Invalid command", false);
                break;
        }
    }

    void CreateTax(String[] context){
        if(context.length < 6){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        double tp;
        double wc;
        boolean plg = false;
        try{
            tp = Double.parseDouble(context[4]);
        } catch (Exception e) {
            PrintError("ERROR: TIPP must be a number", false);
            return;
        }
        try{
            wc = Double.parseDouble(context[5]);
        } catch (Exception e) {
            PrintError("ERROR: WC must be a number", false);
            return;
        }

        if(context.length == 7 && context[6].equals("@p"))
            plg = true;
        
        CreateTaxCommand c = new CreateTaxCommand(context[2], context[3], plg, tp, wc);
        c.setServices(DI.get(ICreator.class));
        Enqueue(c);
    }

    void CreateExemption(String[] context){
        if(context.length < 4){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        CreateExemptionCommand c = new CreateExemptionCommand(context[2], context[3]);
        c.setServices(DI.get(ICreator.class));
        Enqueue(c);
    }

    void CreateIncome(String[] context){
        if(context.length < 4){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        double sm;
        Date dt = Calendar.getInstance().getTime();
        try{
            sm = Double.parseDouble(context[3]);
        } catch (Exception e) {
            PrintError("ERROR: Income value must be a number", false);
            return;
        }
        if(context.length == 5){
            try{
                dt = DateFormat.getInstance().parse(context[4]);
            } catch (Exception e) {
                PrintError("ERROR: Incorrect date format", false);
                return;
            }
        }
        CreateIncomeCommand c = new CreateIncomeCommand(context[2], sm, dt);
        c.setServices(DI.get(ICreator.class), DI.get(IIdentityService.class));
        Enqueue(c);
    }

        //UPDATE

    void Update(String[] context){
        if(context.length < 3){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        String pn = context[1];
        IValueProcesssor p = DI.get(IValueProcesssor.class);
        Object v = null;
        if(context[2].equals("<empty>"))
            context[2] = null;
        v = p.convert(pn, context[2]);
        Class<?> t = p.getType();
        if(!p.isSucceed()){
            PrintError("ERROR: Could not find out the value type for '"+context[2]+"'", false);
            return;
        }
        UpdateCommand c = new UpdateCommand(pn, v, t);
        c.setServices(DI.get(IWriter.class));
        Enqueue(c);
    }

    void Constant(String[] context){
        if(context.length < 2){
            PrintError("ERROR: Not enough params", false);
            return;
        }

        switch(context[1]){

            case "set":
                if(context.length < 4){
                    PrintError("ERROR: Not enough params", false);
                    return;
                }

                double d;
                try
                {
                    d = Double.parseDouble(context[3]);
                } 
                catch(Exception e) 
                {
                    PrintError("ERROR: " +context[3]+ "had to be a number, but was not", false);
                    return;
                }
                SetConstantCommand c = new SetConstantCommand(context[2], d);
                c.setServices(DI.get(IConstantProvider.class));
                Enqueue(c);
            break;

            default:
                PrintError("ERROR: Wrong param " + context[1], false);
            break;
        }
    }

        //DELETE

    void Remove(String[] context){
        if(context.length == 4 && context[1].equals("User"))
            RemoveUser(context);
        boolean all = false;
        if(context.length == 2 && context[1].equals("@a"))
            all = true;
        RemoveCommand c = new RemoveCommand(all);
        c.setServices(DI.get(IWriter.class));
        Enqueue(c);
    }

    void RemoveUser(String[] context){
        RemoveUserCommand rs = new RemoveUserCommand(context[2], context[3]);
        rs.setServices(DI.get(IIdentityService.class));
        Enqueue(rs);
    }

        //IDENTITY

    void Login(String[] context){
        if(context.length < 4){
            PrintError("ERROR: Not enough params", false);
            return;
        }
        AuthorizeUserCommand c = new AuthorizeUserCommand(context[1], context[2], context[3]);
        c.setServices(DI.get(IIdentityService.class));
        Enqueue(c);
    }

    void Logout(String[] context){
        LogoutUserCommand c = new LogoutUserCommand();
        c.setServices(DI.get(IIdentityService.class));
        Enqueue(c);
    }

        //OTHER

    void GenYearlyReport(){
        GenerateYearlyReportCommand c = new GenerateYearlyReportCommand();
        c.setServices(DI.get(IIdentityService.class), DI.get(IEssentials.class)); 
        Enqueue(c);
    }

    //THREADING

    void TerminateThread(Thread thread){
        ThreadPool.remove(thread);
        thread.interrupt();
        TryRunNext();
    }

    void TryRunNext(){
        if(!ThreadPool.isEmpty() && ThreadPool.get(0).getState() == State.NEW)
            ThreadPool.get(0).start();
    }

    void Enqueue(final IObjectiveCommand command){
        Thread th = new Thread(
            new RunableCommand(
                command, 
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ Print(x, true); }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ PrintError(x, true); }
                },
                new Consumer<Thread>() {
                    @Override
                    public void accept(Thread x){ TerminateThread(x); }
                }));
        ThreadPool.add(th);
        TryRunNext();
    }

    void KillAll(){
        if(!ThreadPool.isEmpty())
        ThreadPool.get(0).interrupt();
        ThreadPool.clear();
    }

    public class RunableCommand implements Runnable {
        private IObjectiveCommand command;
        private Consumer<String> out;
        private Consumer<String> outErr;
        private Consumer<Thread> terminate;

        public RunableCommand(IObjectiveCommand command, Consumer<String> output, Consumer<String> errorOutput, Consumer<Thread> terminator) {
           this.command = command;
           out = output;
           outErr = errorOutput;
           terminate = terminator;
        }
  
        public void run() {
            try {
                Logger.INSTANCE.log("Executing: "+command.getClass().getSimpleName(), null);
                CommandResult<Object> res = command.executeAsObjective();
                if(res.IsSucceed){
                    if(res.Result != null)
                        out.accept(res.Message + "\n" + res.Result.toString());
                    else  out.accept(res.Message);
                    Logger.INSTANCE.log("Execution result ==> Success; "+res.Message, LogLevel.Info);
                }
                else{
                    outErr.accept(res.Message);
                    if(res.Message.contains("FATAL:"))
                        Logger.INSTANCE.log("Execution result ==> FATAL; "+res.Message, LogLevel.Fatal);
                    else Logger.INSTANCE.log("Execution result ==> Failure; "+res.Message, LogLevel.Info);
                }
                terminate.accept(Thread.currentThread());
            } catch (Exception e){
                outErr.accept(e.getMessage());
                StringWriter s = new StringWriter();
                PrintWriter w = new PrintWriter(s);
                e.printStackTrace(w);
                String trace = s.getBuffer().toString();
                Logger.INSTANCE.log(e.getMessage()+";\n---TRACE---\n\n" + trace, LogLevel.Fatal);
                terminate.accept(Thread.currentThread());
            }
        }
    }
}
