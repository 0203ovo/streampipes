/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.rdf4j.ontology;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Namespace;
import org.streampipes.model.client.ontology.NodeType;
import org.streampipes.model.client.ontology.OntologyNode;
import org.streampipes.storage.rdf4j.filter.BackgroundKnowledgeFilter;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;
import org.streampipes.storage.rdf4j.util.BackgroundKnowledgeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassHierarchyExecutor extends QueryExecutor {
		
	public ClassHierarchyExecutor(Repository repository)
	{
		super(repository);
	}
	
	private List<OntologyNode> getClasses() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		TupleQueryResult result = getQueryResult(QueryBuilder.getClasses());
		List<OntologyNode> classNodes = new ArrayList<>();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfX = bindingSet.getValue("result");
			classNodes.add(makeNode(valueOfX.stringValue(), NodeType.CLASS));
		}
		return BackgroundKnowledgeFilter.classFilter(classNodes, true);
	}
	
	private String getLabelName(String value)
	{
		if (value.contains("#")) return value.substring(value.indexOf("#")+1);
		else if (value.contains("/")) return value.substring(value.lastIndexOf("/")+1);
		else return value;
	}
	
	private TupleQueryResult getQueryResult(String queryString) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		return executeQuery(queryString);
	}
	
	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> classHierarchy = new ArrayList<>();
		for(OntologyNode node : getClasses())
		{
			List<OntologyNode> children = new ArrayList<>();
			children.addAll(getInstances(node.getId()));
			node.setNodes(children);
			classHierarchy.add(node);
		}
		return classHierarchy;
	}
	
	@SuppressWarnings("unused")
	private List<OntologyNode> getSubclasses(String nodeUri) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> result = new ArrayList<>();
		TupleQueryResult queryResult = getQueryResult(QueryBuilder.getSubclasses(nodeUri));
		
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			Value valueOfX = bindingSet.getValue("s");
			result.add(makeNode(valueOfX.stringValue(), NodeType.CLASS));
		}
		return result;
	}
	
	private OntologyNode makeNode(String id, NodeType nodeType)
	{
		OntologyNode node;
		Optional<Namespace> ns = BackgroundKnowledgeUtils.getNamespace(id);
		node = ns.map(namespace -> new OntologyNode(id, id.replace(namespace.getNamespaceId(), namespace.getPrefix() + ":"), namespace.getPrefix(), namespace.getNamespaceId(), NodeType.CLASS))
						.orElseGet(() -> new OntologyNode(id, getLabelName(id), nodeType));
		
		return node;
	}
	
	private List<OntologyNode> getInstances(String className) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
		List<OntologyNode> result = new ArrayList<>();
		TupleQueryResult queryResult = getQueryResult(QueryBuilder.getInstances(className));
		
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			Value valueOfX = bindingSet.getValue("s");
			result.add(makeNode(valueOfX.toString(), NodeType.INSTANCE));
		}
		return BackgroundKnowledgeUtils.filterDuplicates(result);
	}
}
