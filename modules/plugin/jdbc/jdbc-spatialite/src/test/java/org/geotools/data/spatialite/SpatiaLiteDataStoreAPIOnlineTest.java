/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.spatialite;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.jdbc.JDBCDataStoreAPIOnlineTest;
import org.geotools.jdbc.JDBCDataStoreAPITestSetup;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Point;



/**
 * 
 *
 * @source $URL$
 */
public class SpatiaLiteDataStoreAPIOnlineTest extends JDBCDataStoreAPIOnlineTest {

    @Override
    protected JDBCDataStoreAPITestSetup createTestSetup() {
        return new SpatiaLiteDataStoreAPITestSetup();
    }

    public void testRecreateSchema() throws Exception {
        String featureTypeName = tname("recreated");
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

        // Build feature type
        SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
        ftb.setName(featureTypeName);
        ftb.add(aname("id"), Integer.class);
        ftb.add(aname("name"), String.class);
        ftb.add(aname("the_geom"), Point.class, crs);
        SimpleFeatureType newFT = ftb.buildFeatureType();

        // Crate a schema
        dataStore.createSchema(newFT);
        SimpleFeatureType newSchema = dataStore.getSchema(featureTypeName);
        assertNotNull(newSchema);

        // Delete it
        dataStore.removeSchema(newFT.getTypeName());
        try {
            dataStore.getSchema(featureTypeName);
            fail("Should have thrown an IOException because featureTypeName shouldn't exist");
        } catch(IOException e) {
        }

        // Create the same schema again
        dataStore.createSchema(newFT);
        SimpleFeatureType recreatedSchema = dataStore.getSchema(featureTypeName);
        assertNotNull(recreatedSchema);
    }

    @Override
    public void testTransactionIsolation() throws Exception {
        //super.testTransactionIsolation();
        //JD: In order to allow multiple connections from the same thread (which this test requires) 
        // we need to put the database in read_uncommitted mode, which means transaction isolation 
        // can not be achieved
    }
    
    @Override
    public void testGetFeatureReaderFilterTransaction() throws NoSuchElementException, IOException,
            IllegalAttributeException {
        //super.testGetFeatureReaderFilterTransaction();
    }

}
