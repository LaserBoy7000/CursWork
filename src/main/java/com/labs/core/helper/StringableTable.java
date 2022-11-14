package com.labs.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StringableTable<T> {

    private String res;
    private T[] data;
    private String[][] StringifiedFields;
    private String[] Headers;
    private boolean HasEntity = true;

    public StringableTable(Object[][] cellDate){
        data = null;
        List<Integer> columnsInfo = new ArrayList<Integer>();
        String[][] rawdata = stringify(cellDate, columnsInfo);
        Headers = rawdata[0];
        StringifiedFields = new String[rawdata.length - 1][];
        System.arraycopy(rawdata, 1, StringifiedFields, 0, rawdata.length - 1);
        res = buildTable(rawdata, columnsInfo);
        HasEntity = false;
    }

    public StringableTable(T[] array, boolean ennumerated, int doublePrecision, String... propperties) throws NoSuchFieldError {
        Headers = propperties;
        data = Arrays.copyOf(array, array.length);
        List<Integer> columnsInfo = new ArrayList<Integer>();
        String[][] rawdata = stringify(array, ennumerated, doublePrecision, columnsInfo, propperties);
        if(ennumerated)
            attachEnnumeration(rawdata, columnsInfo);
        StringifiedFields = Arrays.copyOf(rawdata, rawdata.length);
        rawdata = attachNames(rawdata, columnsInfo, ennumerated, propperties);
        Headers = Arrays.copyOf(rawdata[0], rawdata[0].length);
        res = buildTable(rawdata, columnsInfo);
    }

    private void attachEnnumeration(String[][] raw, List<Integer> columnsInfo){
        int c = raw.length-1;
        int lg = (c+"").length();
        for (; c >= 0; c--) {
            String[] nw = new String[raw[c].length + 1];
            System.arraycopy(raw[c], 0, nw, 1, raw[c].length);
            nw[0] = ""+(c+1);
            raw[c] = nw;
        }
        columnsInfo.add(0, lg);
    }

    private String[][] attachNames(String[][] raw, List<Integer> columnsInfo, boolean ennumerated, String... propperties){
        int i = 0;
        String[] title = new String[propperties.length + (ennumerated ? 1 : 0)];
        if(ennumerated)
        {
            i++;
            title[0] = "#";
        }
        for(int st = 0; i < title.length; i++, st++){
            title[i] = propperties[st];
            if(propperties[st].length() > columnsInfo.get(i))
                columnsInfo.set(i, propperties[st].length());
        }

        String[][] ret = new String[raw.length+1][];
        ret[0] = title;
        System.arraycopy(raw, 0, ret, 1, raw.length);
        return ret;
    }

    private String[][] stringify(Object[][] data, List<Integer> estimatedLenght){
        if(data.length == 0)
            return new String[0][];

        int  l = data[0].length;

        estimatedLenght.clear();
        for(int i = 0; i < l; i++)
            estimatedLenght.add(0);

        String[][] rs = new String[data.length][l];
        
        for(int i = 0; i < data.length; i++){
            for(int j = 0; j < l; j++){
                Object o = data[i][j];
                String v = o == null ? "<empty>" : o.toString();
                rs[i][j] = v;
                int lg = estimatedLenght.get(j);
                int ilg = v.length();
                if(ilg > lg)
                    estimatedLenght.set(j, ilg);
            }
        }
        return rs;
    }

    private String[][] stringify(T[] array, boolean ennumerated, int doublePrecision, List<Integer> estimatedLenght, String... propperties) throws NoSuchFieldError {
        ReflectionGetter gt = new ReflectionGetter();
        String[][] dt = new String[array.length][propperties.length];
        
        estimatedLenght.clear();
        for(int i = 0; i < propperties.length; i++)
            estimatedLenght.add(0);

        int r = 0;
        for (Object obj : array) {
            int c = 0;
            for (String string : propperties) {
                Function<Object, Object> g = gt.buildReflectedGetter(obj.getClass(), string);
                if(g == null)
                    throw new NoSuchFieldError("Object of type " + obj.getClass().getSimpleName() + " does not have property " + string);
                Object v = g.apply(obj);
                if(gt.isNumerical(obj.getClass(), string))
                    try
                    {
                        v = String.format("%."+doublePrecision+"f", (double)v);
                    } catch (Exception e) { }

                dt[r][c] = v != null ? v.toString() : "<empty>";
                if(dt[r][c].length() > estimatedLenght.get(c))
                    estimatedLenght.set(c, dt[r][c].length());
                c++;
            }
            r++;
        }
        return dt;
    }

    private String buildTable(String[][] raw, List<Integer> columnsInfo){
        if(raw.length == 0)
            return "";

        int lg = 1;
        for(int t = 0; t < raw[0].length; t++)
            lg += 3 + columnsInfo.get(t);

        StringBuilder ln = new StringBuilder();
        for(int i = 0; i < lg; i++)
            ln.append('-');
        ln.append('\n');

        StringBuilder bld = new StringBuilder();
        bld.append(ln);
        
        for (int r = 0; r < raw.length; r++) {
            lg+= 1;
            bld.append('|');
            for(int i = 0; i < raw[r].length; i++){
                int lgl = 2 + columnsInfo.get(i);
                bld.append(String.format("%-"+lgl+"s", raw[r][i]));
                bld.append('|');
            }
            bld.append('\n');
            bld.append(ln);
        }
        
        return bld.toString();
    }

    @Override
    public String toString(){
        return res;
    }

    @Override
    public boolean equals(Object obj){
        return res.equals(obj.toString());
    }

    @Override
    public int hashCode(){
        return res.hashCode();
    }
    
    public T[] getData(){
        if(data == null)
            return null;
        return Arrays.copyOf(data, data.length);
    }

    public boolean HasEntity() {
        return HasEntity;
    }

    public String[] getHeaders(){
        return Arrays.copyOf(Headers, Headers.length);
    }

    public int size(){
        return data.length;
    }

    public String[][] getFields(){
        return Arrays.copyOf(StringifiedFields, StringifiedFields.length);
    }
}
