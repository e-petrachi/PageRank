package sii;

import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class PageRank {
    private final double DAMPING_FACTOR = 0.5;

    private List params = new ArrayList();
    private static Map<String, String[]> outlinkMap =   new HashMap();
    private static Map<String, String[]> inlinkMap  =   new HashMap();

    public static void main(String[] args) throws IOException {

        PageRank ranking = new PageRank();

        FileInputStream in = new FileInputStream("linkgraph.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        String[] tempStringArray = null;

        int iTemp = 0;

        while ((strLine = br.readLine()) != null) {

            tempStringArray = strLine.split(" ");

            String[] tempValueArray = new String[tempStringArray.length-1];

            for(iTemp=1;iTemp<tempStringArray.length;iTemp++){
                tempValueArray[iTemp-1] = tempStringArray[iTemp];
            }

            outlinkMap.put(tempStringArray[0], tempValueArray);
        }
        in.close();

        setInboundLinks();

        for(int j=1;j<=outlinkMap.size();j++){
            String keytoString = Integer.toString(j);
            System.out.println(keytoString+" "+ranking.rank(keytoString));
        }
    }

    public double rank(String pageId)
    {
        generateParamList(pageId);
        Matrix a = new Matrix(generateMatrix());
        double[][] arrB = new double[params.size()][1];
        for (int i = 0; i < params.size(); i++) {
            arrB[i][0] = 1 - DAMPING_FACTOR;
        }
        Matrix b = new Matrix(arrB);

        Matrix x = a.solve(b);
        int ind = 0;
        int cnt = 0;
        for (Iterator it = params.iterator(); it.hasNext();) {
            String curPage = (String) it.next();
            if (curPage.equals(pageId))
                ind = cnt;
            cnt++;
        }
        return x.getArray()[ind][0];
    }

    private double[][] generateMatrix() {
        double[][] arr = new double[params.size()][params.size()];
        for (int i = 0; i < params.size(); i++) {
            for (int j = 0; j < params.size(); j++) {
                arr[i][j] = getMultiFactor((String) params.get(i),(String) params.get(j));
            }
        }
        return arr;
    }

    private double getMultiFactor(String sourceId, String linkId) {
        if (sourceId.equals(linkId))
            return 1;
        if(!sourceId.equals(linkId) && getInboundLinks(sourceId)!=null ) {
            //System.out.println("source"+sourceId+"\tlinkInd"+linkId);
            String[] inc = getInboundLinks(sourceId);
            for (int i = 0; i < inc.length; i++) {
                if (inc[i].equals(linkId)) {
                    return -1*(DAMPING_FACTOR / getOutboundLinks(linkId).length);
                }
            }
        }
        return 0;
    }

    private void generateParamList(String pageId) {


        if(getInboundLinks(pageId)!=null){
            if (!params.contains(pageId))
                params.add(pageId);

            String[] inc = getInboundLinks(pageId);

            for (int i = 0; i < inc.length; i++) {
                if (!params.contains(inc[i])){
                    generateParamList(inc[i]);
                }
            }
        }
    }

    private String[] getInboundLinks(String pageId) {
        return inlinkMap.get(pageId);
    }

    private static void setInboundLinks(){

        int valueLength;
        String keytoString;
        String[] outlinkValueArray, inlinkValueArrayTemp, tempCopyArray, resultArray;

        for(int i=1; i<=outlinkMap.size();i++){

            keytoString = Integer.toString(i);
            outlinkValueArray= outlinkMap.get(keytoString);
            if(outlinkValueArray!=null){
                valueLength = outlinkValueArray.length;
                for(int j=0; j<valueLength;j++){
                    String[] tempValueArray = new String[1];
                    if(!inlinkMap.containsKey(outlinkValueArray[j])){

                        tempValueArray[0] = keytoString;
                        inlinkMap.put(outlinkValueArray[j],tempValueArray);

                    }
                    else{

                        tempCopyArray = inlinkMap.get(outlinkValueArray[j]);
                        tempValueArray[0] = keytoString;

                        resultArray = mergeArray(tempCopyArray, tempValueArray);
                        inlinkMap.put(outlinkValueArray[j],resultArray);
                    }
                }
            }
        }

    }

    public static String[] mergeArray(String[] ar1, String[] ar2){
        String[] ar3 = Arrays.copyOf(ar1, ar1.length+ar2.length);
        System.arraycopy(ar2, 0, ar3, ar1.length, ar2.length);
        return ar3;
    }

    private String[] getOutboundLinks(String pageId) {
        return outlinkMap.get(pageId);
    }
}
