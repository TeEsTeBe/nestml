/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package org.nest.integration;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import org.junit.Ignore;
import org.junit.Test;
import org.nest.base.ModelbasedTest;
import org.nest.codegeneration.sympy.ODEProcessor;
import org.nest.nestml._ast.ASTNESTMLCompilationUnit;
import org.nest.symboltable.symbols.NeuronSymbol;
import org.nest.symboltable.symbols.VariableSymbol;
import org.nest.utils.FilesHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Tests if the overall transformation solveODE works
 *
 * @author plotnikov
 */
public class ODEProcessorTest extends ModelbasedTest {
  private static final String COND_MODEL_FILE = "src/test/resources/codegeneration/iaf_cond_alpha.nestml";
  private static final String PSC_MODEL_FILE = "src/test/resources/codegeneration/iaf_neuron.nestml";
  private static final String PSC_DELTA_MODEL_FILE = "src/test/resources/codegeneration/iaf_psc_delta.nestml";
  private static final String PSC_NEURON_NAME = "iaf_neuron_nestml";

  private final ODEProcessor testant = new ODEProcessor();

  @Test
  public void testPscModel() throws Exception {
    final Scope scope = processModel(PSC_MODEL_FILE);

    final Optional<NeuronSymbol> neuronSymbol = scope.resolve(
        PSC_NEURON_NAME,
        NeuronSymbol.KIND);

    final Optional<VariableSymbol> y1 = neuronSymbol.get().getVariableByName("y1_G");
    assertTrue(y1.isPresent());
    assertTrue(y1.get().getBlockType().equals(VariableSymbol.BlockType.STATE));
  }

  @Ignore
  @Test
  public void testCondModel() throws Exception {
    processModel(COND_MODEL_FILE);
  }

  @Test
  public void testDeltaModel() throws Exception {
    processModel(PSC_DELTA_MODEL_FILE);
  }

  /**
   * Parses model, builds symboltable, cleanups output folder by deleting tmp file and processes ODEs from model.i
   * @param pathToModel
   * @return
   */
  private Scope processModel(final String pathToModel) {
    final ASTNESTMLCompilationUnit modelRoot = parseNESTMLModel(pathToModel);
    scopeCreator.runSymbolTableCreator(modelRoot);
    final String modelFolder = modelRoot.getFullName();
    final Path outputBase = Paths.get(OUTPUT_FOLDER.toString(), Names.getPathFromQualifiedName(modelFolder));
    FilesHelper.deleteFilesInFolder(outputBase);

    testant.solveODE(modelRoot.getNeurons().get(0), outputBase);

    return scopeCreator.runSymbolTableCreator(modelRoot);
  }

}