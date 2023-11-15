package com.idorsia.research.spirit.core.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssayResultValueDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.service.AssayResultService;
import com.idorsia.research.spirit.core.service.AssayResultValueService;
import com.idorsia.research.spirit.core.service.AssayService;
import com.idorsia.research.spirit.core.service.BiosampleService;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class ExpressionHelper {
	
	@Autowired
	private static BiosampleService biosampleService;
	
	public static ExpressionBuilder createExpressionBuilder(String expr) throws Exception {
		ExpressionBuilder e = new ExpressionBuilder(expr);
		Function round = new Function("round", 2) {
		    @Override
		    public double apply(double... args) {
		        return Math.round(args[0] * Math.pow(10, args[1])) / Math.pow(10, args[1]);
		    }
		};
		e.function(round);
		return e;		
	}
	
	/**
	 * Creates an expression using I1 for the input attributes and O1, O2 for the output
	 * @param expr
	 * @param test
	 * @return
	 * @throws Exception
	 */
	public static Expression createExpression(String expr, AssayDto assay) throws Exception {
		ExpressionBuilder e = createExpressionBuilder(expr);
		AssayService assayService = (AssayService) ContextShare.getContext().getBean("assayService");
		if(assay!=null) {
			Set<String> variables = new HashSet<>();
			int i = 0;
			for(AssayAttributeDto a: assayService.getInputAttributes(assay)) {
				i++;
				if(a.getDataType()==DataType.NUMBER) variables.add("I"+i);
			}
			i = 0;
			for(AssayAttributeDto a: assayService.getOutputAttributes(assay)) {
				i++;
				if(a.getDataType()==DataType.NUMBER) variables.add("O"+i);
			}
			e = e.variables(variables);
		}
		return e.build();
	}
	
	/**
	 * Evaluate the given expression on the result. The Expression must have been created through createExpression
	 * @param expr
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public static double evaluate(Expression expr, AssayResultDto result) throws Exception {
		AssayService assayService = (AssayService) ContextShare.getContext().getBean("assayService");
		AssayResultService assayResultService = (AssayResultService) ContextShare.getContext().getBean("assayResultService");
		AssayResultValueService assayResultValueService = (AssayResultValueService) ContextShare.getContext().getBean("assayResultValueService");
		
		AssayDto assay = result.getAssay();
		int i = 0;
		for(AssayAttributeDto a: assayService.getInputAttributes(assay)) {
			i++;
			AssayResultValueDto rv = assayResultService.getResultValue(result, a);
			if(a.getDataType()==DataType.NUMBER && assayResultValueService.getDoubleValue(rv)!=null) {
				expr.setVariable("I" + i, assayResultValueService.getDoubleValue(rv));
			}
		}
		i = 0;
		for(AssayAttributeDto a: assayService.getOutputAttributes(assay)) {
			i++;
			AssayResultValueDto rv = assayResultService.getResultValue(result, a);			
			if(a.getDataType()==DataType.NUMBER && assayResultValueService.getDoubleValue(rv)!=null) {
				expr.setVariable("O" + i, assayResultValueService.getDoubleValue(rv));
			}
		}
		return expr.evaluate();
	}

	/**
	 * Creates an expression using M1, M2 for the metadata
	 * @param expr
	 * @param biotype
	 * @return
	 * @throws Exception
	 */
	public static Expression createExpression(String expr, BiotypeDto biotype) throws Exception {
		ExpressionBuilder e = createExpressionBuilder(expr);
		
		if(biotype!=null) {
			Set<String> variables = new HashSet<>();
			int i = 0;
			for(BiotypeMetadataDto a: biotype.getMetadatas()) {
				i++;
				if(a.getDatatype()==DataType.NUMBER) {
					variables.add("M"+i);
				}
			}
			e.variables(variables);
		}
		return e.build();
	}
	
	/**
	 * Evaluate the given expression on the biosample. The Expression must have been created through createExpression
	 * @param expr
	 * @param biosample
	 * @return
	 * @throws Exception
	 */
	public static double evaluate(Expression expr, BiosampleDto biosample) throws Exception {
		BiotypeDto biotype = biosample.getBiotype();
		int i = 0;
		for(BiotypeMetadataDto bm: biotype.getMetadatas()) {
			i++;
			String m = biosampleService.getMetadataValue(biosample, bm);		
			if(bm.getDatatype()==DataType.NUMBER && m!=null  && m.length()>0) {
				Double d = MiscUtils.parseDouble(m);
				if(d!=null) {
					expr.setVariable("M" + i, d);
				} else {
					return Double.NaN;
				}
			}
		}
		return expr.evaluate();
	}
}
