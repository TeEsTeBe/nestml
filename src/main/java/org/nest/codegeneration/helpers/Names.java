package org.nest.codegeneration.helpers;

import com.google.common.base.Strings;
import org.nest.commons._ast.ASTVariable;
import org.nest.ode._ast.ASTDerivative;
import org.nest.symboltable.symbols.VariableSymbol;

/**
 * The class is responsible code snippets that access variables, e.g. locally, from state or parameters.
 * TODO: this class can be parametrized to support GSL style instantiation
 * @author plotnikov
 */
public class Names {

  public static String name(final VariableSymbol variableSymbol) {
    return convertToCPPName(variableSymbol.getName());
  }

  public static String name(final ASTDerivative astDerivative ) {
    return convertToCPPName(astDerivative.toString());
  }

  public static String name(final ASTVariable astVariable) {
    return convertToCPPName(astVariable.toString());
  }

  public static String getter(final VariableSymbol variableSymbol) {
    return "get_" + convertToCPPName(variableSymbol.getName());
  }

  public static String setter(final VariableSymbol variableSymbol) {
    return "set_" + convertToCPPName(variableSymbol.getName());
  }

  /**
   * Converts names of the form g_in'' to a compilable C++ identifier: __DDX_g_in
   */
  private static String convertToCPPName(final String varialbeName) {

    // cast is ok, it is hardly possible to have a variable that overflows integer
    int derivativeOrder = (int) varialbeName.chars().filter(ch -> ch == '\'').count();
    if (derivativeOrder > 0) {
      return "__" + Strings.repeat("D", derivativeOrder) + "_" + varialbeName.replaceAll("\'", "");
    }
    else {
      return varialbeName;
    }

  }


}
