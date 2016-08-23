/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package org.nest.spl._cocos;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.logging.Log;
import org.nest.commons._ast.ASTVariable;
import org.nest.spl._ast.ASTAssignment;
import org.nest.spl._ast.ASTDeclaration;
import org.nest.spl._ast.ASTFOR_Stmt;
import org.nest.symboltable.symbols.VariableSymbol;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static de.se_rwth.commons.logging.Log.error;

/**
 * Checks that a variable used in an statement is defined before.
 *
 * @author ippen, plotnikov
 */
public class VariableNotDefinedBeforeUse implements
    SPLASTAssignmentCoCo,
    SPLASTDeclarationCoCo,
    SPLASTFOR_StmtCoCo {

  public static final String ERROR_CODE = "SPL_VARIABLE_NOT_DEFINED_BEFORE_USE";
  private static final String ERROR_MSG_FORMAT = "Variable '%s' not defined yet. It is defined at line '%d'";

  @Override
  public void check(final ASTFOR_Stmt forstmt) {
    String fullName = forstmt.getVar();
    check(fullName, forstmt);
  }

  @Override
  public void check(final ASTAssignment assignment) {
      check(assignment.getLhsVarialbe().toString(), assignment);
  }

  @Override
  public void check(final ASTDeclaration decl) {
    checkArgument(decl.getEnclosingScope().isPresent(), "Run symboltable creator.");
    final Scope scope = decl.getEnclosingScope().get();

    if (decl.getExpr().isPresent()) {
      final List<String> varsOfCurrentDecl = Lists.newArrayList(decl.getVars());
      final List<ASTVariable> variablesNamesRHS = ASTNodes.getSuccessors(decl.getExpr().get(), ASTVariable.class);;
      // check, if variable of the left side is used in the right side, e.g. in decl-vars

      for (final ASTVariable variable: variablesNamesRHS) {
        final String varRHS = variable.toString();
        final VariableSymbol variableSymbol =VariableSymbol.resolve(varRHS, scope);
        // e.g. x real = 2 * x
        if (varsOfCurrentDecl.contains(varRHS)) {
          final String logMsg = "Cannot use variable '%s' in the assignment of its own declaration.";
          error(ERROR_CODE + ":" + String.format(logMsg, varRHS),
              decl.get_SourcePositionStart());
        }
        else if (variable.get_SourcePositionStart().compareTo(variableSymbol.getAstNode().get().get_SourcePositionStart()) < 0) {
          // y real = 5 * x
          // x integer = 1
          final String logMsg = "Cannot use variable '%s' before its usage.";
          error(ERROR_CODE + ":" + String.format(logMsg, variable),
              decl.get_SourcePositionStart());
        }

      }

    }

  }

  protected void check(final String varName, final ASTNode node) {
    checkArgument(node.getEnclosingScope().isPresent(), "No scope assigned. Please, run symboltable creator.");
    final Scope scope = node.getEnclosingScope().get();

    Optional<VariableSymbol> varOptional = scope.resolve(varName, VariableSymbol.KIND);

    if(varOptional.isPresent()) {
      // exists
      if (node.get_SourcePositionStart().compareTo(varOptional.get().getSourcePosition()) < 0) {
        Log.error(ERROR_CODE + ":" +
                String
                    .format(ERROR_MSG_FORMAT, varName, varOptional.get().getSourcePosition().getLine()),
            node.get_SourcePositionEnd());
      }
    }
    else {
      Log.warn(ERROR_CODE +  "Variable " + varName + " couldn't be resolved.");
    }

  }

}
