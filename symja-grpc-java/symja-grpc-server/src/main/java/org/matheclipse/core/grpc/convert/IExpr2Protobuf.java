package org.matheclipse.core.grpc.convert;

import org.matheclipse.core.grpc.PBAST;
import org.matheclipse.core.grpc.PBComplex;
import org.matheclipse.core.grpc.PBComplexNum;
import org.matheclipse.core.grpc.PBExpr;
import org.matheclipse.core.grpc.PBFraction;
import org.matheclipse.core.grpc.PBInteger;
import org.matheclipse.core.grpc.PBNum;
import org.matheclipse.core.grpc.PBPattern;
import org.matheclipse.core.grpc.PBString;
import org.matheclipse.core.grpc.PBSymbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.protobuf.ByteString;

public class IExpr2Protobuf {
	public static IExpr2Protobuf CONST = new IExpr2Protobuf();

	public PBExpr convert(final IExpr x) {
		org.matheclipse.core.grpc.PBExpr.Builder exprBuilder = PBExpr.newBuilder();
		if (x instanceof ISymbol) {
			return exprBuilder.setIdentifier(convertSymbol((ISymbol) x)).build();
		}
		if (x instanceof INumber) {
			if (x instanceof INum) {
				return exprBuilder.setNumeric(convertNum((INum) x)).build();
			}
			if (x instanceof IInteger) {
				return exprBuilder.setBigInteger(convertInteger((IInteger) x)).build();
			}
			if (x instanceof IFraction) {
				return exprBuilder.setBigFraction(convertFraction((IFraction) x)).build();
			}
			if (x instanceof IComplex) {
				return exprBuilder.setBigComplex(convertComplex((IComplex) x)).build();
			}
			if (x instanceof IComplexNum) {
				return exprBuilder.setComplexNumeric(convertComplexNum((IComplexNum) x)).build();
			}
		}
		if (x instanceof IAST) {
			return exprBuilder.setAst(convertAST((IAST) x)).build();
		}
		if (x instanceof IPattern) {
			return exprBuilder.setPattern(convertPattern((IPattern) x)).build();
		}
		if (x instanceof IStringX) {
			return exprBuilder.setStr(convertStringX((IStringX) x)).build();
		}
		return null;
	}

	public PBSymbol convertSymbol(final ISymbol val) {
		return PBSymbol.//
				newBuilder().//
				setName(val.getSymbolName()).//
				build();
	}

	public PBString convertStringX(final IStringX val) {
		return PBString.//
				newBuilder().//
				setContent(val.toString()).//
				build();
	}

	public PBPattern convertPattern(final IPattern val) {
		org.matheclipse.core.grpc.PBPattern.Builder patternBuilder = PBPattern.newBuilder();
		patternBuilder.setDefaultValue(val.isPatternDefault());
		IExpr condition = val.getCondition();
		if (condition != null) {
			patternBuilder.setCondition(convert(condition));
		}
		IExpr value = val.getDefaultValue();
		if (value != null) {
			patternBuilder.setDefault(convert(value));
		}
		return patternBuilder.build();
	}

	public PBNum convertNum(final INum val) {
		return PBNum.//
				newBuilder().//
				setValue(val.getReal()).//
				build();
	}

	public PBComplexNum convertComplexNum(final IComplexNum val) {
		return PBComplexNum.//
				newBuilder().//
				setReValue(val.getReal()).//
				setImValue(val.getImaginary()).//
				build();
	}

	public PBInteger convertInteger(final IInteger val) {
		ByteString bytes = ByteString.copyFrom(val.toBigNumerator().toByteArray());
		return PBInteger.//
				newBuilder().//
				setValue(bytes).//
				build();
	}

	public PBFraction convertFraction(final IFraction val) {
		return PBFraction.newBuilder().//
				setNumerator(convertInteger(val.getNumerator())).//
				setDenominator(convertInteger(val.getDenominator())).//
				build();
	}

	public PBFraction convertFraction(final IRational val) {
		return PBFraction.newBuilder().//
				setNumerator(convertInteger(val.getNumerator())).//
				setDenominator(convertInteger(val.getDenominator())).//
				build();
	}

	public PBComplex convertComplex(final IComplex val) {
		return PBComplex.newBuilder().//
				setReValue(convertFraction(val.getRealPart())).//
				setImValue(convertFraction(val.getImaginaryPart())).//
				build();
	}

	public PBAST convertAST(final IAST val) {
		org.matheclipse.core.grpc.PBAST.Builder localBuilder = PBAST.newBuilder().setHead(convert(val.head()));
		for (int i = 1; i < val.size(); i++) {
			PBExpr temp = convert(val.get(i));
			localBuilder.addArg(temp);
		}
		return localBuilder.build();
	}
}
