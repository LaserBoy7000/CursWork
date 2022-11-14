package com.labs.UI.impl.jfx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.ICommand;
import com.labs.UIAPI.IObjectiveCommand;
import com.labs.core.diagnostics.LogLevel;
import com.labs.core.diagnostics.Logger;
import com.labs.core.helper.StringableTable;

public class AsynchronousMachine {

    private List<Thread> ThreadPool = new ArrayList<>();
    private Consumer<StringableTable<?>> OutputTable = null;
    private Consumer<String> Print = null;
    private Consumer<String> PrintError = null;

    public void setOutputTable(Consumer<StringableTable<?>> out){
        OutputTable = out;
    }

    public void setOutputMessage(Consumer<String> message){
        Print = message;
    }

    public void setErrorMessage(Consumer<String> error){
        PrintError = error;
    }

    private void TerminateThread(Thread thread){
        ThreadPool.remove(thread);
        thread.interrupt();
        TryRunNext();
    }

    private void TryRunNext(){
        if(!ThreadPool.isEmpty() && ThreadPool.get(0).getState() == State.NEW)
            ThreadPool.get(0).start();
    }

    public void EnqueueTabular(final ICommand<StringableTable<?>> command){
        EnqueueTabularCustom(command, OutputTable);
    }

    public void EnqueueTabularCustom(final ICommand<StringableTable<?>> command, Consumer<StringableTable<?>> out){
        Thread th = new Thread(
            new RunableCommand(
                command, 
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ Print.accept(x); }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ PrintError.accept(x); }
                },
                new Consumer<Thread>() {
                    @Override
                    public void accept(Thread x){ TerminateThread(x); }
                },
                out,
                true));
        ThreadPool.add(th);
        TryRunNext();
    }

    public void Enqueue(final IObjectiveCommand command){
        Thread th = new Thread(
            new RunableCommand(
                command, 
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ Print.accept(x); }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String x){ PrintError.accept(x); }
                },
                new Consumer<Thread>() {
                    @Override
                    public void accept(Thread x){ TerminateThread(x); }
                },
                null,
                false));
        ThreadPool.add(th);
        TryRunNext();
    }



    private class RunableCommand implements Runnable {
        private IObjectiveCommand command;
        private Consumer<String> out;
        private Consumer<String> outErr;
        private Consumer<Thread> terminate;
        private Consumer<StringableTable<?>> outputTable;
        private boolean toTable;

        public RunableCommand(IObjectiveCommand command, Consumer<String> output, Consumer<String> errorOutput, Consumer<Thread> terminator, Consumer<StringableTable<?>> outputTable, boolean toTable) {
           this.command = command;
           out = output;
           outErr = errorOutput;
           terminate = terminator;
           this.toTable = toTable;
           this.outputTable = outputTable;
        }
  
        public void run() {
            try {
                if(!toTable)
                    runVoid();
                else runTable();
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

        private void runVoid(){
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
        }

        private void runTable(){
            Logger.INSTANCE.log("Executing: "+command.getClass().getSimpleName(), null);
            @SuppressWarnings("unchecked")
            CommandResult<StringableTable<?>> res = ((ICommand<StringableTable<?>>)command).execute();
            if(res.IsSucceed){
                out.accept(res.Message);
                outputTable.accept(res.Result);
                Logger.INSTANCE.log("Execution result ==> Success; "+res.Message, LogLevel.Info);
            }
            else
            {
                outErr.accept(res.Message);
                if(res.Message.contains("FATAL:"))
                    Logger.INSTANCE.log("Execution result ==> FATAL; "+res.Message, LogLevel.Fatal);
                else Logger.INSTANCE.log("Execution result ==> Failure; "+res.Message, LogLevel.Info);
            }
            terminate.accept(Thread.currentThread());
        }
    }
}
