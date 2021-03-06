/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.internal;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

/**
 *  Unit tests for the DomainImpl class.
 */
public class DomainImplTest {
     
    private static final QName SERVICE = new QName("Service");
    private ServiceReference _service;
    private DomainImpl _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new DomainImpl(new QName("test"),
                new DefaultServiceRegistry(),
                new LocalExchangeBus(),
                null);
        _service = _domain.registerService(SERVICE, new MockHandler());
    }
    
    @Test
    public void testCreateExchange() {
        Exchange inOnly = _domain.createExchange(_service, ExchangeContract.IN_ONLY);
        Assert.assertEquals(ExchangePattern.IN_ONLY, inOnly.getContract().getServiceOperation().getExchangePattern());
        Exchange inOut = _domain.createExchange(_service, ExchangeContract.IN_OUT, new MockHandler());
        Assert.assertEquals(ExchangePattern.IN_OUT, inOut.getContract().getServiceOperation().getExchangePattern());
    }
    
    @Test
    public void testRegisterServiceWithoutInterface() {
        ServiceReference service = _domain.registerService(
                new QName("no-interface"), new MockHandler());
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation(
                ServiceInterface.DEFAULT_OPERATION));
    }
    
    @Test
    public void testRegisterServiceWithInterface() {
        ServiceReference service = _domain.registerService(new QName("my-interface"), 
                new MockHandler(), JavaService.fromClass(MyInterface.class));
        // default interface should be used, which has one operation - process()
        Assert.assertNotNull(service.getInterface());
        Assert.assertTrue(service.getInterface().getOperations().size() == 1);
        Assert.assertNotNull(service.getInterface().getOperation("myOperation"));
    }
    
    @Test
    public void testGetService() {
        ServiceReference service = _domain.getService(SERVICE);
        Assert.assertNotNull(service);
    }
    
}

interface MyInterface {
    void myOperation(String msg);
}
