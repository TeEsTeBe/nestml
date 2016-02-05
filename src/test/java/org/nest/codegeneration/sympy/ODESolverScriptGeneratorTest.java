/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package org.nest.codegeneration.sympy;

import org.junit.Ignore;
import org.junit.Test;
import org.nest.base.ModelTestBase;
import org.nest.nestml._ast.ASTNESTMLCompilationUnit;
import org.nest.nestml._parser.NESTMLParser;
import org.nest.nestml._symboltable.NESTMLScopeCreator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.nest.codegeneration.sympy.ODESolverScriptGenerator.generateSympyODEAnalyzer;

/**
 * Tests that the solver script is generated from an ODE based model.
 *
 * @author plotnikov
 */
public class ODESolverScriptGeneratorTest extends ModelTestBase {
  public static final String PATH_TO_PSC_MODEL
      = "src/test/resources/codegeneration/iaf_neuron_ode.nestml";
  public static final String PATH_TO_COND_MODEL
      = "src/test/resources/codegeneration/iaf_cond_alpha.nestml";
  public static final String PATH_TO_COND_IMPLICIT_MODEL
      = "src/test/resources/codegeneration/iaf_cond_alpha_implicit.nestml";
  private static final String OUTPUT_FOLDER = "target";

  @Test
  public void generateSymPySolverForPSCModel() throws IOException {
    generateScriptForModel(PATH_TO_PSC_MODEL);
  }

  @Test
  public void generateSymPySolverForCondModel() throws IOException {
    generateScriptForModel(PATH_TO_COND_MODEL);
  }

  @Ignore("Enable as soon as the script can handle it")
  @Test
  public void generateSymPySolverForCondImplicitModel() throws IOException {
    generateScriptForModel(PATH_TO_COND_IMPLICIT_MODEL);
  }

  private void generateScriptForModel(final String pathToModel) throws IOException {
    final NESTMLParser p = new NESTMLParser(TEST_MODEL_PATH);
    final Optional<ASTNESTMLCompilationUnit> root = p.parse(pathToModel);

    assertTrue(root.isPresent());

    final NESTMLScopeCreator nestmlScopeCreator = new NESTMLScopeCreator(TEST_MODEL_PATH);
    nestmlScopeCreator.runSymbolTableCreator(root.get());

    final Optional<Path> generatedScript = generateSympyODEAnalyzer(
        root.get().getNeurons().get(0),
        Paths.get(OUTPUT_FOLDER));

    assertTrue(generatedScript.isPresent());
  }

}