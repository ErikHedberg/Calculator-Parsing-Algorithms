/**
 * Created by snowb on 4/13/2016.
 */
public class ExtendedMath {

    public static double asinh(double value) {
        return Math.log(value + Math.sqrt(Math.pow(value,2)+1));
    }

    public static double acosh(double value) {
        if(value < 1) {
            return Double.NaN;
        }else {
            return Math.log(value + Math.sqrt(Math.pow(value, 2) - 1));
        }
    }

    public static double atanh(double value) {
        if(Math.abs(value) > 1) {
            return Double.NaN;
        }else {
            return 0.5 * Math.log((1 + value) / (1 - value));
        }
    }

    public static double cot(double value) {
        return 1/Math.tan(value);
    }

    public static double acot(double value) {
        return Math.atan(1/value);
    }

    public static double coth(double value) {
        return Math.cosh(value)/Math.sinh(value);
    }

    public static double acoth(double value) {
        if(Math.abs(value) == 1) {
            return Double.POSITIVE_INFINITY;
        }else if(Math.abs(value) < 1) {
            return Double.NaN;
        }else {
            return 0.5 * Math.log((value + 1) / (value - 1));
        }
    }

    public static double sec(double value) {
        return 1/Math.cos(value);
    }

    public static double asec(double value) {
        if(Math.abs(value) < 1) {
            return Double.NaN;
        }else {
            return Math.acos(1 / value);
        }
    }

    public static double sech(double value) {
        return 1/Math.cosh(value);
    }

    public static double asech(double value) {
        if(Math.abs(value) > 1) {
            return Double.NaN;
        }else {
            return Math.log((1 + Math.sqrt(1 - Math.pow(value, 2))) / value);
        }
    }

    public static double csc(double value) {
        return 1/Math.sin(value);
    }

    public static double acsc(double value) {
        if(Math.abs(value) < 1) {
            return Double.NaN;
        }else {
            return Math.asin(1 / value);
        }
    }

    public static double csch(double value) {
        return 1/Math.sinh(value);
    }

    public static double acsch(double value) {
        if(value == 0) {
            return Double.NaN;
        }else {
            return Math.log((1 + Math.sqrt(1 + Math.pow(value, 2))) / value);
        }
    }

    public static double root(double base,double value) {
        return Math.pow(10,Math.log10(value)/base);
    }

    public static double logn(double base,double value) {
        if(base > 1) {
            return Math.log(value) / Math.log(base);
        }else{
            return Double.NaN;
        }
    }

    public static double factorial(double value) {
        if(value == 0 || value == 1) {
            return 1;
        }
        if(value > 0 && Math.round(value) == value) {
            double total = 1;
            for(int i=(int)value;i>0;i--) {
                total *= i;
            }
            return total;
        }else {
            return Double.NaN;
        }
    }

    public static double sum(double i,double t,String f) {
        String fSim = Parser.simplifyAlgebraic(f);
        /* Simplified Cases */
        if (fSim.equals("1")) {
            return t + 1 - i;
        }else if(fSim.equals("k")) {//Arithmetic series
            if (i == 0) {
                return (t * (t + 1)) / 2;
            } else {
                return ((t + 1 - i) * (t + i)) / 2;
            }
        }else if(fSim.equals("k^2") && i==0) {//Square pyramidal numbers
            return ((t * (t + 1)) * (2 * t + 1)) / 6;
        }else if(fSim.equals(Parser.digitRegex+"^k") && i < t) { //Geometric series
            double base = Double.parseDouble(fSim.replaceAll("^k",""));
            if(base == 1) {
                return t + 1 -i;
            }else{
                return (Math.pow(base,i) - Math.pow(base,t+1)) / (1 - base);
            }
        }else{
            /* Resort to looping */
            double total = 0;
            if(i > t) {
                for (int k = (int) i; k <= t; k++) {
                    total += Parser.simplify(fSim.replaceAll("k", Double.toString(k)));
                }
            }else{
                return 0;
            }
            return total;
        }
    }

    public static double prod(double i,double t,String f) {
        String fSim = Parser.simplifyAlgebraic(f);
        double total = 1;
        if(i > t) {
            for (int k = (int) i; k <= t; k++) {
                total *= Parser.simplify(fSim.replaceAll("k", Double.toString(k)));
                if(total == 0) {
                    return 0;
                }
            }
        }else{
            return 1;
        }
        return total;
    }
}
