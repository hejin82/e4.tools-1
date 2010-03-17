/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.tools.emf.ui.internal.common;


import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.e4.tools.emf.ui.common.IClassContributionProvider;
import org.eclipse.e4.tools.emf.ui.common.IClassContributionProvider.ContributionResultHandler;
import org.eclipse.e4.tools.emf.ui.common.IClassContributionProvider.Filter;

public class ClassContributionCollector {
	private CopyOnWriteArrayList<IClassContributionProvider> providers = new CopyOnWriteArrayList<IClassContributionProvider>();
	
	public void addContributor(IClassContributionProvider contributor) {
		System.err.println("Add Contributor: " + contributor);
		providers.add(contributor);
	}
	
	public void removeContributor(IClassContributionProvider contributor) {
		System.err.println("Remove Contributor: " + contributor);
		providers.remove(contributor);
	}
	
	public void findContributions(Filter filter, ContributionResultHandler resultHandler) {
		
		for( IClassContributionProvider contributor: providers ) {
			contributor.findContribution(filter, resultHandler);
		}
	}
}
