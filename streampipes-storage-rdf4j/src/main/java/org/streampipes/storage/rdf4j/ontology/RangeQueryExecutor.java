/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.client.ontology.PrimitiveRange;
import org.streampipes.model.client.ontology.QuantitativeValueRange;
import org.streampipes.model.client.ontology.Range;
import org.streampipes.model.client.ontology.RangeType;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;

import java.util.Arrays;
import java.util.List;

public class RangeQueryExecutor extends QueryExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(RangeQueryExecutor.class);

  private static final String RANGE_TYPE_RDFS_ENUMERATION = "http://sepa.event-processing.org/sepa#Enumeration";
  public static final List<String> RANGE_TYPE_RDFS_LITERAL = Arrays.asList("http://www.w3.org/2001/XMLSchema#string",
          "http://www.w3.org/2001/XMLSchema#boolean",
          "http://www.w3.org/2001/XMLSchema#integer",
          "http://www.w3.org/2001/XMLSchema#double",
          "http://www.w3.org/2001/XMLSchema#float");
  private static final String RANGE_TYPE_RDFS_QUANTITATIVE_VALUE = "http://schema.org/QuantitativeValue";


  private Range range;
  private RangeType rangeType;

  private String propertyId;
  private String rangeId;
  private List<String> rangeTypeRdfs;

  private boolean includeValues;
  private String instanceId;

  public RangeQueryExecutor(Repository repository, String propertyId, String rangeId, List<String> rangeTypeRdfs) {
    super(repository);
    this.propertyId = propertyId;
    this.rangeTypeRdfs = rangeTypeRdfs;
    this.includeValues = false;
    this.rangeId = rangeId;
    prepare();
  }

  public RangeQueryExecutor(Repository repository, String propertyId, String rangeId, List<String> rangeTypeRdfs, String instanceId) {
    super(repository);
    this.propertyId = propertyId;
    this.rangeTypeRdfs = rangeTypeRdfs;
    this.instanceId = instanceId;
    this.includeValues = true;
    this.rangeId = rangeId;
    prepare();
  }

  private void prepare() {
    prepareRangeType();

    if (rangeType == RangeType.PRIMITIVE) {
      range = new PrimitiveRange(rangeId);
      if (includeValues) {
        try {
          TupleQueryResult result = executeQuery(QueryBuilder.getPrimitivePropertyValue(instanceId, propertyId));
          LOG.info(QueryBuilder.getPrimitivePropertyValue(instanceId, propertyId));
          while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Value value = bindingSet.getValue("value");

            ((PrimitiveRange) range).setValue(value.stringValue());
          }
        } catch (QueryEvaluationException | RepositoryException
                | MalformedQueryException e) {
          e.printStackTrace();
        }
      }
    } else if (rangeType == RangeType.ENUMERATION) {
      //TODO implement enumerated type
    } else if (rangeType == RangeType.QUANTITATIVE_VALUE) {
      range = new QuantitativeValueRange();
      if (includeValues) {
        try {
          TupleQueryResult result = executeQuery(QueryBuilder.getQuantitativeValueRange(propertyId));
          while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Value minValue = bindingSet.getValue("minValue");
            Value maxValue = bindingSet.getValue("maxValue");
            Value unitCode = bindingSet.getValue("unitCode");

            ((QuantitativeValueRange) range).setMinValue(Integer.parseInt(minValue.stringValue()));
            ((QuantitativeValueRange) range).setMaxValue(Integer.parseInt(maxValue.stringValue()));
            ((QuantitativeValueRange) range).setUnitCode(unitCode.stringValue());


          }
        } catch (QueryEvaluationException | RepositoryException
                | MalformedQueryException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  private void prepareRangeType() {
    if (rangeTypeRdfs.stream().anyMatch(p -> p.startsWith(RANGE_TYPE_RDFS_ENUMERATION))) {
      rangeType = RangeType.ENUMERATION;
    } else if (rangeTypeRdfs.stream().anyMatch(p -> p.startsWith(RANGE_TYPE_RDFS_QUANTITATIVE_VALUE))) {
      rangeType = RangeType.QUANTITATIVE_VALUE;
    } else if (RANGE_TYPE_RDFS_LITERAL.stream().anyMatch(rt -> rangeId.startsWith(rt))) {
      rangeType = RangeType.PRIMITIVE;
    } else {
      rangeType = RangeType.RDFS_CLASS;
    }
  }

  public Range getRange() {
    return range;
  }

  public RangeType getRangeType() {
    return rangeType;
  }

}
