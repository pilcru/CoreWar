package gui;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import core.Operation;

public class TableauMem extends DefaultComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> text;

	public TableauMem() {
		super();
		this.text = new ArrayList<String>();
	}

	public TableauMem(Operation[] O) {
		super();
		this.text = new ArrayList<String>();
		for (int i = 0; i < 800; i++) {
			text.add(O[799 - i].ins.toString() + O[799 - i].OA.m
					+ O[799 - i].OA.a.toString() + O[799 - i].OB.m
					+ O[799 - i].OB.a.toString());
		}
	}

	@Override
	public Object getElementAt(int index) {
		return text.get(index);
	}

	@Override
	public int getIndexOf(Object element) {
		return text.indexOf(element);
	}

	@Override
	public int getSize() {
		return text.size();
	}

}
