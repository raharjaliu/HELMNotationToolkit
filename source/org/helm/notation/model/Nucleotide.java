/*******************************************************************************
 * Copyright C 2012, The Pistoia Alliance
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.helm.notation.model;

import org.helm.notation.MonomerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * This is a data model class for nucleotide
 * @author ZHANGTIANHONG
 */
public class Nucleotide implements Serializable {

    public static final int STARTING_POSITION_TYPE = 1;
    public static final int MIDDLE_POSITION_TYPE = 2;
    public static final int ENDING_POSITION_TYPE = 3;

    private int positionType = MIDDLE_POSITION_TYPE;

    private String symbol;
    private boolean modified;
    private String notation;
    private String naturalAnalog;


    public Nucleotide() {
    }

    public Nucleotide(String symbol, String notation) {
        this.symbol = symbol;
        this.notation = notation;
    }

    public Nucleotide(String notation, int postionType) {
        this.notation = notation;
        this.positionType = postionType;
    }

    public Nucleotide(String symbol, String notation, int postionType) {
        this.symbol = symbol;
        this.notation = notation;
        this.positionType = postionType;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isModified() {
        if (null != getNotation() ) {
         
            //has modifier
            if (getNotation().contains("["))
                return true;
            
            //has X as base
            if (getNotation().contains("(X)"))
                return true;
            
            //ends with ")", i.e.,no phophate
            if (getNotation().endsWith(")"))
                return true;
            } 
      
        return false;
    }

    /**
     * Returns true if the nucleotide is unmodified except for a missing
     * phosphate group.  If the nucleotide notation is empty, it returns true.
     * @return true or false
     */
    public boolean unmodifiedWithoutPhosphate() {
        if (getNotation() == null) {
            return true;
        } else {
            //System.out.println("Contains [ " + getNotation().contains("[") + "\tContainsX " + getNotation().contains("["));
            if (getNotation().contains("[") || getNotation().contains("X")) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Deprecated
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getNaturalAnalog() {
        int start = getNotation().indexOf("(");
        int end = getNotation().indexOf(")");
        if (start <0)
            return "X";
        String baseNotation = getNotation().substring(start + 1, end);
        String baseSymbol = baseNotation.replaceAll("\\[|\\]", "");
        try {
            Map<String, Monomer> monomers = MonomerFactory.getInstance().getMonomerDB().get(Monomer.NUCLIEC_ACID_POLYMER_TYPE);
            Monomer m = monomers.get(baseSymbol);
            return m.getNaturalAnalog();
        } catch (Exception e) {
            System.out.println("Unable to get natural analog for " + baseSymbol);
            return "X";
        }
    }

    @Deprecated
    public void setNaturalAnalog(String naturalAnalog) {
        this.naturalAnalog = naturalAnalog;
    }

    /**
     * return the phosphate monomer of this nucleotide
     * @return phosphate monomer
     */
    public Monomer getPhosphateMonomer() {
        String phosphateSymbol = getPhosphateSymbol();
        if (phosphateSymbol != null && !phosphateSymbol.equalsIgnoreCase("")) {
            try {
                Map<String, Monomer> monomers = MonomerFactory.getInstance().getMonomerDB().get(Monomer.NUCLIEC_ACID_POLYMER_TYPE);
                Monomer m = monomers.get(phosphateSymbol);
                return m;
            } catch (Exception ex) {
                System.out.println("Unable to get natural analog for " + phosphateSymbol);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * get the base monomer, the return value could be null if this nucleotide does not has a base
     * @return base monomer, could be null
     */
    public Monomer getBaseMonomer() {

        String baseSymbol = getBaseSymbol();
        if (baseSymbol != null && !baseSymbol.equalsIgnoreCase("")) {
            try {
                Map<String, Monomer> monomers = MonomerFactory.getInstance().getMonomerDB().get(Monomer.NUCLIEC_ACID_POLYMER_TYPE);
                Monomer m = monomers.get(baseSymbol);
                return m;
            } catch (Exception ex) {
                System.out.println("Unable to get natural analog for " + baseSymbol);
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * get the sugar monomer, the return value could be null if the "nucleotide" does not has a sugar 
     * @return sugar monomer
     */
    public Monomer getSugarMonomer() {
        String sugarSymbol = getSugarSymbol();
        if (sugarSymbol != null && !sugarSymbol.equalsIgnoreCase("")) {
            try {
                Map<String, Monomer> monomers = MonomerFactory.getInstance().getMonomerDB().get(Monomer.NUCLIEC_ACID_POLYMER_TYPE);
                Monomer m = monomers.get(sugarSymbol);
                return m;
            } catch (Exception ex) {
                System.out.println("Unable to get natural analog for " + sugarSymbol);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This method returns the HELM notation for nucleotide linker
     * @return linker notation
     */
    public String getLinkerNotation() {
        String pSymbol = getPhosphateSymbol();
        String result = null;
        if (null == pSymbol || pSymbol.length() ==0) {
            result = "";
        } else {
           if (pSymbol.length() >1)
               result =  "["+pSymbol+"]";
           else
               result = pSymbol;
        }
        return result;
    }

    /**
     * This method returns the HELM notation for nucleoside
     * @return nucleodie notation
     */
    public String getNucleosideNotation() {
        int linkerLen = getLinkerNotation().length();
        return notation.substring(0, notation.length() - linkerLen );
    }

    /**
     * find the phosphate symbol, if it is a modified monomer, the "[" and "]" will be removed
     * @return
     */
    private String getPhosphateSymbol() {
        String phosphateSymbol = null;
        //nucleotide notation
        String ncNotation = getNotation();
        //nucleotide has base
        if (ncNotation.contains("(")) {
            int start = ncNotation.indexOf(")");
            phosphateSymbol = ncNotation.substring(start + 1);
            phosphateSymbol = phosphateSymbol.replaceAll("\\[|\\]", "");
        } else {
            if (getPositionType() == ENDING_POSITION_TYPE) {
                if (ncNotation.startsWith("[")) {
                    phosphateSymbol = ncNotation.substring(ncNotation.indexOf("]") + 1);
                    phosphateSymbol = phosphateSymbol.replaceAll("\\[|\\]", "");
                } else {
                    phosphateSymbol = ncNotation.substring(1);
                }
            } else {
                if (ncNotation.endsWith("]")) {
                    char[] chars = ncNotation.toCharArray();
                    for (int i= chars.length; i>0; i--) {
                        String letter = String.valueOf(chars[i-1]);
                        if (letter.equals("[")) {
                            phosphateSymbol = ncNotation.substring(i);
                            phosphateSymbol = phosphateSymbol.replaceAll("\\[|\\]", "");
                            break;
                        }
                    }
                } else {
                    int pos = ncNotation.length() -1;
                    phosphateSymbol = ncNotation.substring(pos);
                }
            }
        }
        return phosphateSymbol;
    }

    /**
     * find the sugar symbol, if it is a modified monomer, the "[" and "]" will be removed
     * @return
     */
    private String getSugarSymbol() {
        String sugarSymbol = null;
        //nucleotide notation
        String ncNotation = getNotation();
        //nucleotide has base
        if (ncNotation.contains("(")) {
            int start = ncNotation.indexOf("(");
            sugarSymbol = ncNotation.substring(0, start);
            sugarSymbol = sugarSymbol.replaceAll("\\[|\\]", "");
        } else {
            if (getPositionType() == STARTING_POSITION_TYPE) {
                if (ncNotation.endsWith("]")) {
                    char[] chars = ncNotation.toCharArray();
                    for (int i= chars.length; i>0; i--) {
                        String letter = String.valueOf(chars[i-1]);
                        if (letter.equals("[")) {
                            sugarSymbol = ncNotation.substring(0, i);
                            sugarSymbol = sugarSymbol.replaceAll("\\[|\\]", "");
                            break;
                        }
                    }
                } else {
                    sugarSymbol = ncNotation.substring(0, ncNotation.length() -1);
                    sugarSymbol = sugarSymbol.replaceAll("\\[|\\]", "");
                }
            } else {
                if (ncNotation.startsWith("[")) {
                    sugarSymbol = ncNotation.substring(0, ncNotation.indexOf("]"));
                    sugarSymbol = sugarSymbol.replaceAll("\\[|\\]", "");
                } else {
                    sugarSymbol = ncNotation.substring(0,1);
                }
            }
        }
        return sugarSymbol;
    }

    /**
     * find the base symbol, if it is a modified monomer, the "[" and "]" will be removed
     * @return
     */
    private String getBaseSymbol() {
        String ncNotation = getNotation();
        if (!ncNotation.contains("(")) {
            return null;
        }
        int start = ncNotation.indexOf("(");
        int end = ncNotation.indexOf(")");

        String baseSymbol = ncNotation.substring(start + 1, end);
        baseSymbol = baseSymbol.replaceAll("\\[|\\]", "");
        return baseSymbol;
    }
}
