package org.matheclipse.core.grpc.convert;

import java.math.BigInteger;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.grpc.PBAST;
import org.matheclipse.core.grpc.PBComplex;
import org.matheclipse.core.grpc.PBComplexNum;
import org.matheclipse.core.grpc.PBExpr;
import org.matheclipse.core.grpc.PBExpr.AtomCase;
import org.matheclipse.core.grpc.PBFraction;
import org.matheclipse.core.grpc.PBInteger;
import org.matheclipse.core.grpc.PBNum;
import org.matheclipse.core.grpc.PBPattern;
import org.matheclipse.core.grpc.PBString;
import org.matheclipse.core.grpc.PBSymbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.protobuf.ByteString;

public class Protobuf2IExpr {
	public static Protobuf2IExpr CONST = new Protobuf2IExpr();

	// private static org.matheclipse.core.grpc.PBSymbol.Builder symbolBuilder = PBSymbol.newBuilder();
	// private static org.matheclipse.core.grpc.PBExpr.Builder exprBuilder = PBExpr.newBuilder();
	// private static org.matheclipse.core.grpc.PBInteger.Builder intBuilder = PBInteger.newBuilder();
	// private static org.matheclipse.core.grpc.PBFraction.Builder fractionBuilder = PBFraction.newBuilder();
	// private static org.matheclipse.core.grpc.PBAST.Builder astBuilder = PBAST.newBuilder();

	public IExpr convert(final PBExpr message) {
		if (message.equals(PBExpr.getDefaultInstance())) {
			return null;
		}
		AtomCase atomCase = message.getAtomCase();
		switch (atomCase) {
		case BIG_INTEGER:
			return convertInteger(message.getBigInteger());
		case BIG_FRACTION:
			return convertFraction(message.getBigFraction());
		case BIG_COMPLEX:
			return convertComplex(message.getBigComplex());
		case NUMERIC:
			return convertNumeric(message.getNumeric());
		case COMPLEX_NUMERIC:
			return convertComplexNumeric(message.getComplexNumeric());
		case AST:
			return convertAST(message.getAst());
		case IDENTIFIER:
			return convertSymbol(message.getIdentifier());
		case STR:
			return convertString(message.getStr());
		case PATTERN:
			return convertPattern(message.getPattern());
		}
		return null;
	}

	ISymbol convertSymbol(final PBSymbol message) {
		if (message.equals(PBSymbol.getDefaultInstance())) {
			return null;
		}
		return F.$s(message.getName());
	}

	IStringX convertString(final PBString message) {
		return F.$str(message.getContent());
	}

	IPattern convertPattern(final PBPattern message) {
		boolean defaultValue = message.getDefaultValue();
		if (defaultValue) {
			return F.$p(convertSymbol(message.getSymbol()), //
					convert(message.getCondition()), //
					message.getDefaultValue());
		}
		return F.$p(convertSymbol(message.getSymbol()), //
				convert(message.getCondition()), //
				convert(message.getDefault()));
	}

	IInteger convertInteger(PBInteger message) {
		ByteString bytes = message.getValue();
		return F.integer(new BigInteger(bytes.toByteArray()));
	}

	IFraction convertFraction(PBFraction message) {
		PBInteger numerator = message.getNumerator();
		PBInteger denominator = message.getDenominator();
		return F.fraction(convertInteger(numerator), convertInteger(denominator));
	}

	IComplex convertComplex(PBComplex message) {
		PBFraction re = message.getReValue();
		PBFraction im = message.getImValue();
		return F.complex(convertFraction(re), convertFraction(im));
	}

	INum convertNumeric(PBNum message) {
		return F.num(message.getValue());
	}

	IComplexNum convertComplexNumeric(PBComplexNum message) {
		return F.complexNum(message.getReValue(), message.getImValue());
	}

	IAST convertAST(final PBAST message) {
		PBExpr head = message.getHead();
		int size = message.getArgCount();
		IASTAppendable ast = F.ast(convert(head), size + 1, false);
		for (int i = 0; i < size; i++) {
			ast.append(convert(message.getArg(i)));
		}
		return ast;
	}

}
