/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.core.grpc.samples.server;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.grpc.PBExpr;
import org.matheclipse.core.grpc.SymjaServiceGrpc;
import org.matheclipse.core.grpc.convert.IExpr2Protobuf;
import org.matheclipse.core.grpc.convert.Protobuf2IExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import io.grpc.stub.StreamObserver;

public class SymjaServiceImpl extends SymjaServiceGrpc.SymjaServiceImplBase {
	private final static boolean DEBUG = true;
	private ExprEvaluator fEvaluator;

	// private OutputFormFactory fOutputFactory;

	/**
	 * Use pretty printer for expression output n print stream
	 */
	// private boolean fPrettyPrinter;

	SymjaServiceImpl() {
		fEvaluator = new ExprEvaluator(false, 100);
		// DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
		// DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
		// fOutputFactory = OutputFormFactory.get(true, false, decimalFormat);
	}

	/**
	 * Evaluates the given string-expression and returns the result in <code>OutputForm</code>
	 * 
	 * @param inputExpression
	 * @return
	 */
	public IExpr interpreter(final IExpr expression) {
		IExpr result;
		final StringWriter buf = new StringWriter();
		try {
			// if (fSeconds <= 0) {
			result = fEvaluator.eval(expression);
			// } else {
			// result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true);
			// }
			if (result != null) {
				return result;
			}
		} catch (final AbortException re) {
			if (DEBUG) {
				re.printStackTrace();
			}
			// try {
			// return printResult(F.Aborted);
			// } catch (IOException e) {
			// Validate.printException(buf, e);
			// return "";
			// }
		} catch (final SyntaxError se) {
			if (DEBUG) {
				se.printStackTrace();
			}
			// String msg = se.getMessage();
			// System.err.println();
			// System.err.println(msg);
			// return "";
		} catch (final RuntimeException re) {
			if (DEBUG) {
				re.printStackTrace();
			}
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				Validate.printException(buf, me);
			} else {
				Validate.printException(buf, re);
			}
		} catch (final Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			Validate.printException(buf, e);
		} catch (final OutOfMemoryError e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			Validate.printException(buf, e);
		} catch (final StackOverflowError e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			Validate.printException(buf, e);
		}
		return F.$Aborted;
	}

	@Override
	public void eval(PBExpr expr, StreamObserver<PBExpr> responseObserver) {

		IExpr request = Protobuf2IExpr.CONST.convert(expr);
		IExpr result = interpreter(request);
		// System.out.println(request.toString());

		// Send and commit
		responseObserver.onNext(IExpr2Protobuf.CONST.convert(result));
		responseObserver.onCompleted();
	}
}
