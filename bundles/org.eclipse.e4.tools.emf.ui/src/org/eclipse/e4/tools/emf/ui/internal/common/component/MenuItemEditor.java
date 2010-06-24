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
package org.eclipse.e4.tools.emf.ui.internal.common.component;

import org.eclipse.core.databinding.conversion.Converter;

import org.eclipse.core.databinding.UpdateValueStrategy;

import org.eclipse.e4.tools.emf.ui.internal.common.component.dialogs.FindImportElementDialog;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.e4.tools.emf.ui.internal.common.ModelEditor;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.Messages;
import org.eclipse.e4.tools.emf.ui.internal.common.component.dialogs.MenuItemIconDialogEditor;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.MUILabel;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class MenuItemEditor extends AbstractComponentEditor {

	private Composite composite;
	private Image menuImage;
	private EMFDataBindingContext context;
	protected IProject project;

	public MenuItemEditor(EditingDomain editingDomain, ModelEditor editor, IProject project) {
		super(editingDomain,editor);
		this.project = project;
	}

	@Override
	public Image getImage(Object element, Display display) {
			if (menuImage == null) {
				try {
					menuImage = loadSharedImage(display, new URL("platform:/plugin/org.eclipse.e4.tools.emf.ui/icons/full/modelelements/MenuItem.gif")); //$NON-NLS-1$
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return menuImage;
	}

	@Override
	public Composite getEditor(Composite parent, Object object) {
		if (composite == null) {
			context = new EMFDataBindingContext();
			composite = createForm(parent, context, getMaster());
		}
		getMaster().setValue(object);
		return composite;
	}

	private Composite createForm(Composite parent, EMFDataBindingContext context, WritableValue master) {
		parent = new Composite(parent, SWT.NONE);
		parent.setLayout(new GridLayout(3, false));

		IWidgetValueProperty textProp = WidgetProperties.text(SWT.Modify);
		IWidgetValueProperty checkProp = WidgetProperties.selection();
		IWidgetValueProperty enabled = WidgetProperties.enabled();
		
		if( getEditor().isModelFragment() ) {
			ControlFactory.createFindImport(parent, this, context);			
			return parent;
		}
		
		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.MenuItemEditor_Id);

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200, t), EMFEditProperties.value(getEditingDomain(), ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID).observeDetail(getMaster()));
		}
		
		if (this.getClass() != MenuItemEditor.class) {
			// ------------------------------------------------------------
			{
				Label l = new Label(parent, SWT.NONE);
				l.setText(Messages.MenuItemEditor_Type);

				ComboViewer viewer = new ComboViewer(parent);
				viewer.setContentProvider(new ArrayContentProvider());
				viewer.setInput(new ItemType[] { ItemType.CHECK, ItemType.PUSH, ItemType.RADIO });
				GridData gd = new GridData();
				gd.horizontalSpan = 2;
				viewer.getControl().setLayoutData(gd);
				IObservableValue itemTypeObs = EMFEditProperties.value(getEditingDomain(), MenuPackageImpl.Literals.ITEM__TYPE).observeDetail(master);
				context.bindValue(ViewerProperties.singleSelection().observe(viewer), itemTypeObs);
			}
		}
		
		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.MenuItemEditor_Label);

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200, t), EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__LABEL).observeDetail(master));
		}

		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.MenuItemEditor_Tooltip);

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200, t), EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__TOOLTIP).observeDetail(master));
		}

		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText(Messages.MenuItemEditor_IconURI);

			Text t = new Text(parent, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			context.bindValue(textProp.observeDelayed(200, t), EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__ICON_URI).observeDetail(master));

			final Button b = new Button(parent, SWT.PUSH | SWT.FLAT);
			b.setImage(getImage(t.getDisplay(), SEARCH_IMAGE));
			b.setText(Messages.MenuItemEditor_Find);
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MenuItemIconDialogEditor dialog = new MenuItemIconDialogEditor(b.getShell(), project, getEditingDomain(),(MMenuItem) getMaster().getValue());
					dialog.open();
				}
			});
		}
		
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText("Enabled");
			
			Button b = new Button(parent, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.BEGINNING,GridData.CENTER,false,false,2,1));
			context.bindValue(checkProp.observe(b), EMFEditProperties.value(getEditingDomain(), MenuPackageImpl.Literals.ITEM__ENABLED).observeDetail(getMaster()));
		}

		{
			Label l = new Label(parent, SWT.NONE);
			l.setText("Selected");
			
			Button b = new Button(parent, SWT.CHECK);
			b.setLayoutData(new GridData(GridData.BEGINNING,GridData.CENTER,false,false,2,1));
			context.bindValue(checkProp.observe(b), EMFEditProperties.value(getEditingDomain(), MenuPackageImpl.Literals.ITEM__SELECTED).observeDetail(getMaster()));
			
			UpdateValueStrategy t2m = new UpdateValueStrategy();
			t2m.setConverter(new Converter(boolean.class, ItemType.class) {

				public Object convert(Object fromObject) {
					return null;
				}
			});
			UpdateValueStrategy m2t = new UpdateValueStrategy();
			m2t.setConverter(new Converter(ItemType.class, boolean.class) {
				
				public Object convert(Object fromObject) {
					return fromObject == ItemType.CHECK || fromObject == ItemType.RADIO;
				}
			});
			
			context.bindValue(enabled.observe(b), EMFEditProperties.value(getEditingDomain(), MenuPackageImpl.Literals.ITEM__TYPE).observeDetail(getMaster()),t2m,m2t);

		}
		

		createFormSubTypeForm(parent, context, master);

		ControlFactory.createTagsWidget(parent, this);
		
		return parent;
	}

	protected abstract void createFormSubTypeForm(Composite parent, EMFDataBindingContext context, WritableValue master);

	@Override
	public IObservableList getChildList(Object element) {
		return null;
	}

	@Override
	public String getDetailLabel(Object element) {
		MUILabel label = (MUILabel) element;
		if (label.getLabel() != null && label.getLabel().trim().length() > 0) {
			return label.getLabel();
		}
		return null;
	}

	@Override
	public FeaturePath[] getLabelProperties() {
		return new FeaturePath[] { FeaturePath.fromList(UiPackageImpl.Literals.UI_LABEL__LABEL) };
	}
}