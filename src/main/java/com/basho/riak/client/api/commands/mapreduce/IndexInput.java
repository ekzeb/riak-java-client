package com.basho.riak.client.api.commands.mapreduce;

import com.basho.riak.client.core.query.Namespace;

public class IndexInput implements MapReduceInput
{
	private final Namespace namespace;
	private final String index;
	private final IndexCriteria criteria;

	public IndexInput(Namespace namespace, String index, IndexCriteria criteria)
	{
		this.namespace = namespace;
		this.index = index;
		this.criteria = criteria;
	}

	public Namespace getNamespace()
	{
		return namespace;
	}

	public String getIndex()
	{
		return index;
	}

	public IndexCriteria getCriteria()
	{
		return criteria;
	}

	static interface IndexCriteria
	{
	}

	static class RangeCriteria<V> implements IndexCriteria
	{

		private final V begin;
		private final V end;

		public RangeCriteria(V begin, V end)
		{
			this.begin = begin;
			this.end = end;
		}

		public V getBegin()
		{
			return begin;
		}

		public V getEnd()
		{
			return end;
		}

	}

	static class MatchCriteria<V> implements IndexCriteria
	{

		private final V value;

		public MatchCriteria(V value)
		{
			this.value = value;
		}

		public V getValue()
		{
			return value;
		}

	}
}
