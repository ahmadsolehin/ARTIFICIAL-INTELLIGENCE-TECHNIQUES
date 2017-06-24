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
package org.jenetics.diagram;

import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import org.jenetics.stat.DoubleMomentStatistics;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class CandleStickPoint {
	public final double mean;
	public final double variance;
	public final double skewness;
	public final double kurtosis;
	public final double median;
	public final double low;
	public final double high;
	public final double min;
	public final double max;

	private CandleStickPoint(
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis,
		final double median,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		this.mean = mean;
		this.variance = variance;
		this.skewness = skewness;
		this.kurtosis = kurtosis;
		this.median = median;
		this.low = low;
		this.high = high;
		this.min = min;
		this.max = max;
	}

	public static CandleStickPoint of(
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis,
		final double median,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		return new CandleStickPoint(
			mean,
			variance,
			skewness,
			kurtosis,
			median,
			low,
			high,
			min,
			max
		);
	}

	public static <T> Collector<T, ?, CandleStickPoint>
	toCandleStickPoint(final ToDoubleFunction<T> function) {
		return Collector.of(
			Statistics::new,
			(r, t) -> r.accept(function.applyAsDouble(t)),
			Statistics::combine,
			Statistics::toPoint
		);
	}

	public static <T> Collector<T, ?, CandleStickPoint[]>
	toCandleStickPoint(final ToDoubleFunction<T> f1, final ToDoubleFunction<T> f2) {
		return Collector.of(
			() -> new Statistics[]{new Statistics(), new Statistics()},
			(r, t) -> {
				r[0].accept(f1.applyAsDouble(t));
				r[1].accept(f2.applyAsDouble(t));
			},
			(a, b) -> {
				a[0].combine(b[0]);
				a[1].combine(b[1]);
				return a;
			},
			s -> new CandleStickPoint[]{s[0].toPoint(), s[1].toPoint()}
		);
	}

	private static final class Statistics {
		private final DoubleMomentStatistics data = new DoubleMomentStatistics();
		private final ExactQuantile quantile = new ExactQuantile();

		void accept(final double value) {
			data.accept(value);
			quantile.accept(value);
		}

		Statistics combine(final Statistics other) {
			data.combine(other.data);
			quantile.combine(other.quantile);

			return this;
		}

		CandleStickPoint toPoint() {
			return CandleStickPoint.of(
				data.getMean(),
				data.getVariance(),
				data.getSkewness(),
				data.getKurtosis(),
				quantile.quantile(0.5),
				quantile.quantile(0.25),
				quantile.quantile(0.75),
				data.getMin(),
				data.getMax()
			);
		}
	}


}
