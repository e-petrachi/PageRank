package sii;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] args) throws IOException {

        PageRank ranking = new PageRank();

        // Importo la topografia del grafo dal file linkgraph.txt

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

           ranking.putOutlinkMap(tempStringArray,0, tempValueArray);
        }
        in.close();

        ranking.setInboundLinks();

        System.out.println("-------------- PAGERANK --------------");
        System.out.println();
        for(int j=1;j<=ranking.outlinkMapSize();j++){
            String keytoString = Integer.toString(j);
            System.out.println("Nodo " + keytoString + "| PageRank => " + ranking.rank(keytoString));
        }
    }
}
