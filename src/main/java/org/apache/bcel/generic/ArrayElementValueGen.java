/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ElementValue;

public class ArrayElementValueGen extends ElementValueGen
{
	// J5TODO: Should we make this an array or a list? A list would be easier to
	// modify ...
	private List<ElementValueGen> evalues;

	public ArrayElementValueGen(ConstantPoolGen cp)
	{
		super(ARRAY, cp);
		evalues = new ArrayList<ElementValueGen>();
	}

	public ArrayElementValueGen(int type, ElementValue[] datums,
			ConstantPoolGen cpool)
	{
		super(type, cpool);
		if (type != ARRAY)
			throw new RuntimeException(
					"Only element values of type array can be built with this ctor - type specified: " + type);
		this.evalues = new ArrayList<ElementValueGen>();
		for (int i = 0; i < datums.length; i++)
		{
			evalues.add(ElementValueGen.copy(datums[i], cpool, true));
		}
	}

	/**
	 * Return immutable variant of this ArrayElementValueGen
	 */
	public ElementValue getElementValue()
	{
		ElementValue[] immutableData = new ElementValue[evalues.size()];
		int i = 0;
		for (Iterator<ElementValueGen> iter = evalues.iterator(); iter.hasNext();)
		{
			ElementValueGen element = iter.next();
			immutableData[i++] = element.getElementValue();
		}
		return new ArrayElementValue(type, immutableData, cpGen
				.getConstantPool());
	}

	/**
	 * @param value
	 * @param cpool
	 */
	public ArrayElementValueGen(ArrayElementValue value, ConstantPoolGen cpool,
			boolean copyPoolEntries)
	{
		super(ARRAY, cpool);
		evalues = new ArrayList<ElementValueGen>();
		ElementValue[] in = value.getElementValuesArray();
		for (int i = 0; i < in.length; i++)
		{
			evalues.add(ElementValueGen.copy(in[i], cpool, copyPoolEntries));
		}
	}

	public void dump(DataOutputStream dos) throws IOException
	{
		dos.writeByte(type); // u1 type of value (ARRAY == '[')
		dos.writeShort(evalues.size());
		for (Iterator<ElementValueGen> iter = evalues.iterator(); iter.hasNext();)
		{
			ElementValueGen element = iter.next();
			element.dump(dos);
		}
	}

	public String stringifyValue()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (Iterator<ElementValueGen> iter = evalues.iterator(); iter.hasNext();)
		{
			ElementValueGen element = iter.next();
			sb.append(element.stringifyValue());
			if (iter.hasNext())
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public List<ElementValueGen> getElementValues()
	{
		return evalues;
	}

	public int getElementValuesSize()
	{
		return evalues.size();
	}

	public void addElement(ElementValueGen gen)
	{
		evalues.add(gen);
	}
}