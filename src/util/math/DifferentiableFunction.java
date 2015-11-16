package util.math;

import network.analysis.Debug;

public abstract class DifferentiableFunction implements MathFunction1D {
	
	
	//public abstract double apply(double in); ==> in MathFunction1D ...
	public abstract double applyDO(double out);
	public abstract double applyD(double in);
	public abstract double applyInverse(double out);
	public abstract double applyIntegral(double in);
	public abstract DifferentiableFunction getInverse();
	
	
	@Override
	public abstract String toString();
	public static DifferentiableFunction fromString(String in) {
		if (in.equals("tanh")) return tanh;
		else if (in.equals("artanh")) return artanh;
		else if (in.equals("tanhAlmost")) return tanhAlmost;
		else if (in.equals("logisticSigmoid")) return logisticSigmoid;
		else if (in.equals("logSigInv")) return logSigInv;
		else if (in.equals("none")) return linear;
		else if (in.equals("linear")) return linear;
		else if (in.equals("square")) return square;
		else if (in.equals("sqrt")) return sqrt;
		else if (in.equals("abs")) return abs;
		else if (in.equals("signum")) return signum;
		else if (in.startsWith("limit(") && ! in.contains(")."))
		{
			String firstNsecond = in.substring(6, in.length()-1);
			int sep = firstNsecond.indexOf(';');
			String first = firstNsecond.substring(0, sep);
			String second = firstNsecond.substring(sep+1);
            Debug.out(first);
            Debug.out(second);
			double min = Double.parseDouble(first);
			double max = Double.parseDouble(second);
			return limit(min,max);
		}
		else if (in.contains(".")) 
		{
			int indexOpen = in.indexOf('(');
			int indexClose = in.indexOf(')');
			
			int sep = in.indexOf('.');
			while (indexOpen<sep && sep< indexClose)
				sep = in.indexOf('.',sep+1);
			if (sep==-1)
				return linear;
			
			String first = in.substring(0, sep);
			String second = in.substring(sep+1);
			return fromString(first).compose(fromString(second));
		}
		else return linear;
	}
	
	public DifferentiableFunction compose(final DifferentiableFunction with) {
		final DifferentiableFunction thiss = this;
		return new DifferentiableFunction() {

			@Override
			public double apply(double arg) {
				return thiss.apply(with.apply(arg));
			}

			@Override
			public double applyDO(double in) {
				return Double.NaN;
			}

			@Override
			public double applyD(double in) {
				return with.applyD(in)*thiss.applyD(with.apply(in));
			}

			@Override
			public double applyInverse(double in) {
				return with.applyInverse(thiss.applyInverse(in));
			}

			@Override @Deprecated // TODO not finished! check out the SUBSTITUTION RULE (wiki?)
			public double applyIntegral(double in) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String toString() {
				return thiss.toString()+"."+with.toString();
			}

			@Override
			public DifferentiableFunction getInverse() {
				return with.getInverse().compose(thiss.getInverse());
			}};
	}

	public static DifferentiableFunction tanh = new DifferentiableFunction(){
		@Override public double apply(double in) { // normal function
			return Math2.tanh(in);			}
		@Override public double applyDO(double out) { // derivative given the output
			return Math2.tanhDO(out);				}
		@Override public double applyD(double in) { // derivative given the input
			return Math2.tanhD(in);				}
		@Override public String toString() { return "tanh";}
		@Override public double applyInverse(double out) { 
			if (out==1) return Double.POSITIVE_INFINITY;
			return .5*(Math.log1p(out)-Math.log1p(-out)); } // = log(1+in)-log(1+ -in) = log((1+in)/(1-in))
		@Override
		public double applyIntegral(double in) {
			return Math.log(Math2.cosh(in));
		}
		@Override
		public DifferentiableFunction getInverse() {
			return artanh;
		}
	};
	
	public static DifferentiableFunction artanh = new DifferentiableFunction(){
		@Override public double apply(double in) { // normal function
			return .5 * Math.log((1+in)/(1-in));			}
		@Override public double applyDO(double out) { // derivative given the output
			double in = applyInverse(out);
			return applyD(in);				}
		@Override public double applyD(double in) { // derivative given the input
			return 1/(1-in*in);				}
		@Override public String toString() { return "artanh";}
		@Override public double applyInverse(double out) { 
			return Math.tanh(out); } // = log(1+in)-log(1+ -in) = log((1+in)/(1-in))
		@Override
		public double applyIntegral(double in) {
			return .5*Math.log1p(-in*in)+in*apply(in);
		}
		@Override
		public DifferentiableFunction getInverse() {
			return tanh;
		}
	};
	public static DifferentiableFunction tanhAlmost = new DifferentiableFunction(){
		private static final double approx = .999;
		@Override public double apply(double in) { // normal function
			return Math2.tanh(in)/approx;			}
		@Override public double applyDO(double out) { // derivative given the output
			return Math2.tanhDO(out)/approx;				}
		@Override public double applyD(double in) { // derivative given the input
			return Math2.tanhD(in)/approx;				}
		@Override public String toString() { return "tanhAlmost";}
		@Override public double applyInverse(double out) { 
//			if (in==1) return Double.POSITIVE_INFINITY;
			out *= approx;
			return .5*(Math.log1p(out)-Math.log1p(-out)); } // = log(1+in)-log(1+ -in) = log((1+in)/(1-in))
		@Override @Deprecated // is this correct?
		public double applyIntegral(double in) {
			return Math.log(Math2.cosh(in))/approx;
		}
		@Override
		public DifferentiableFunction getInverse() {
			return artanh;
		}
	};
	public static DifferentiableFunction logisticSigmoid= new DifferentiableFunction(){
		@Override public double apply(double in) {
			return Math2.sigmoid(in);			}
		@Override public double applyDO(double out) {
			return Math2.sigmoidDO(out);				}
		@Override public double applyD(double in) {
			return Math2.sigmoidD(in);				}
		@Override public String toString() { return "logisticSigmoid";}
		@Override public double applyInverse(double out) { 
			if (out==1) return Double.POSITIVE_INFINITY; // instead of diision by 0 error
			return Math.log(out)-Math.log1p(-out); } // = log(in/(1-in)); 
		@Override
		public double applyIntegral(double in) {
			return Math.log1p(Math.exp(in)); // = log(1+Math.exp(in))
		}
		@Override
		public DifferentiableFunction getInverse() {
			return logSigInv;
		}
	};
	public static DifferentiableFunction logSigInv = new DifferentiableFunction(){
		@Override public double apply(double in) {
			return -Math.log(1/in-1);			}
		@Override public double applyDO(double out) {
			double in = applyInverse(out);
			return applyD(in);				}
		@Override public double applyD(double in) {
			return 1/(in-in*in);				}
		@Override public String toString() { return "logSigInv";}
		@Override public double applyInverse(double out) { 
			return Math2.sigmoid(out); } // = log(in/(1-in)); 
		@Override
		public double applyIntegral(double in) {
			return Math.log1p(-in)-in*Math.log(1/in-1); // = log(1+Math.exp(in))
		}
		@Override
		public DifferentiableFunction getInverse() {
			return logisticSigmoid;
		}
	};
	public static DifferentiableFunction linear = new DifferentiableFunction(){
		@Override public double apply(double in) {
			return in;			}
		@Override public double applyDO(double out) {
			return 1;				}
		@Override public double applyD(double in) {
			return 1;				}
		@Override public String toString() { return "linear";}
		@Override public double applyInverse(double out) { return out; }
		@Override
		public double applyIntegral(double in) {
			return .5*in*in;
		}
		@Override
		public DifferentiableFunction getInverse() {
			return linear;
		}
	};
	public static final DifferentiableFunction square = new DifferentiableFunction() {
		@Override public double apply(double arg) {
			return arg*arg;
		}
		@Override public double applyDO(double out) {
			return applyD(applyInverse(out));
		}
		@Override public double applyD(double in) {
			return 2*in;
		}
		@Override 	public double applyInverse(double out) {
			return Math.sqrt(out);
		}
		@Override public String toString() { return "square";	}
		@Override
		public double applyIntegral(double in) {
			return in*in*in/3;
		}
		@Override
		public DifferentiableFunction getInverse() {
			return null;
		}
	};
	public static final DifferentiableFunction sqrt = new DifferentiableFunction() {
		@Override public double apply(double arg) {
			return Math.sqrt(arg);
		}
		@Override public double applyDO(double out) {
			return 1/(2*out);
		}
		@Override public double applyD(double in) {
			return 1/(2*Math.sqrt(in));
		}
		@Override 	public double applyInverse(double out) {
			return out*out;
		}
		@Override public String toString() { return "sqrt";	}
		@Override
		public double applyIntegral(double in) {
			return 2/3*Math.pow(in, 3/2);
		}
		@Override
		public DifferentiableFunction getInverse() {
			return square;
		}
	};
	public static final DifferentiableFunction abs = new DifferentiableFunction() {
		@Override public double apply(double arg) {
			return Math.abs(arg);
		}
		@Override public String toString() {
			return "abs";
		}
		
		@Override public double applyInverse(double out) {
			return Double.NaN;
		}
		
		@Override public double applyIntegral(double in) {
			return in*Math.abs(in)/2;
		}
		
		@Override public double applyDO(double out) {
			return 1;
		}
		
		@Override public double applyD(double in) {
			return Math.signum(in);
		}
		@Override
		public DifferentiableFunction getInverse() {
			return linear;
		}
	};
	public static final DifferentiableFunction signum = new DifferentiableFunction() {
		@Override public double apply(double arg) {
			return Math.signum(arg);
		}
		@Override public String toString() {
			return "signum";
		}
		
		@Override public double applyInverse(double out) {
			return Double.NaN;
		}
		
		@Override public double applyIntegral(double in) {
			return Math.abs(in);
		}
		
		@Override public double applyDO(double out) {
			return 0;
		}
		
		@Override public double applyD(double in) {
			return 0;
		}
		@Override
		public DifferentiableFunction getInverse() {
			return signum;
		}
	};

	public static final DifferentiableFunction limit(final double min, final double max){
		return new DifferentiableFunction() {
			@Override public double apply(double arg) {
				return Math2.limit(min,arg,max);
			}
			@Override public String toString() {
				return "limit("+min+";"+max+")";
			}
			
			@Override public double applyInverse(double out) {
				return out;
			}
			
			@Override public double applyIntegral(double in) {
				return .5*in*in;
			}
			
			@Override public double applyDO(double out) {
				return 1;
			}
			
			@Override public double applyD(double in) {
				return 1;
			}
			@Override
			public DifferentiableFunction getInverse() {
				return linear;
			}
		};
	}

}
