/*
 * Java Genetic Algorithm Library (jenetics-3.3.0).
 * Copyright (c) 2007-2015 Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

import static org.jenetics.util.Seq.toSeq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SeqTest {

	@Test
	public void collector() {
		final int size = 10_000;
		final Random random = RandomRegistry.getRandom();

		final List<Double> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(random.nextDouble());
		}

		final Seq<Double> seq = list.stream().collect(toSeq());
		Assert.assertEquals(list, seq.asList());
	}

	@Test
	public void empty() {
		Assert.assertNotNull(Seq.EMPTY);
		Assert.assertNotNull(Seq.empty());
		Assert.assertSame(Seq.EMPTY, Seq.empty());
		Assert.assertEquals(Seq.EMPTY.length(), 0);
		Assert.assertEquals(Seq.empty().asList().size(), 0);
	}

	@Test
	public void zeroLengthSameAsEmpty() {
		Assert.assertSame(Seq.of(), Seq.empty());
	}

}
