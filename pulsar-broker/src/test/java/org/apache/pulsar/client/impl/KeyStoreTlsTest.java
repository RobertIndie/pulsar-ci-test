/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.client.impl;

import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.BROKER_KEYSTORE_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.BROKER_KEYSTORE_PW;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.BROKER_TRUSTSTORE_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.BROKER_TRUSTSTORE_NO_PASSWORD_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.CLIENT_KEYSTORE_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.CLIENT_KEYSTORE_PW;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.CLIENT_TRUSTSTORE_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.CLIENT_TRUSTSTORE_NO_PASSWORD_FILE_PATH;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.CLIENT_TRUSTSTORE_PW;
import static org.apache.pulsar.broker.auth.MockedPulsarServiceBaseTest.KEYSTORE_TYPE;
import java.util.Collections;
import org.apache.pulsar.common.util.keystoretls.KeyStoreSSLContext;
import org.apache.pulsar.common.util.keystoretls.SSLContextValidatorEngine;
import org.testng.annotations.Test;

@Test(groups = "broker-impl")
public class KeyStoreTlsTest {

    @Test(timeOut = 300000)
    public void testValidate() throws Exception {
        KeyStoreSSLContext serverSSLContext = new KeyStoreSSLContext(KeyStoreSSLContext.Mode.SERVER,
                null,
                KEYSTORE_TYPE,
                BROKER_KEYSTORE_FILE_PATH,
                BROKER_KEYSTORE_PW,
                false,
                KEYSTORE_TYPE,
                CLIENT_TRUSTSTORE_FILE_PATH,
                CLIENT_TRUSTSTORE_PW,
                true,
                null,
                null);
        serverSSLContext.createSSLContext();

        KeyStoreSSLContext clientSSLContext = new KeyStoreSSLContext(KeyStoreSSLContext.Mode.CLIENT,
                null,
                KEYSTORE_TYPE,
                CLIENT_KEYSTORE_FILE_PATH,
                CLIENT_KEYSTORE_PW,
                false,
                KEYSTORE_TYPE,
                BROKER_TRUSTSTORE_FILE_PATH,
                BROKER_KEYSTORE_PW,
                false,
                null,
                // set client's protocol to TLSv1.2 since SSLContextValidatorEngine.validate doesn't handle TLSv1.3
                Collections.singleton("TLSv1.2"));
        clientSSLContext.createSSLContext();

        SSLContextValidatorEngine.validate(clientSSLContext::createSSLEngine, serverSSLContext::createSSLEngine);
    }

    @Test(timeOut = 300000)
    public void testValidateKeyStoreNoPwd() throws Exception {
        KeyStoreSSLContext serverSSLContext = new KeyStoreSSLContext(KeyStoreSSLContext.Mode.SERVER,
                null,
                KEYSTORE_TYPE,
                BROKER_KEYSTORE_FILE_PATH,
                BROKER_KEYSTORE_PW,
                false,
                KEYSTORE_TYPE,
                CLIENT_TRUSTSTORE_NO_PASSWORD_FILE_PATH,
                null,
                true,
                null,
                null);
        serverSSLContext.createSSLContext();

        KeyStoreSSLContext clientSSLContext = new KeyStoreSSLContext(KeyStoreSSLContext.Mode.CLIENT,
                null,
                KEYSTORE_TYPE,
                CLIENT_KEYSTORE_FILE_PATH,
                CLIENT_KEYSTORE_PW,
                false,
                KEYSTORE_TYPE,
                BROKER_TRUSTSTORE_NO_PASSWORD_FILE_PATH,
                null,
                false,
                null,
                // set client's protocol to TLSv1.2 since SSLContextValidatorEngine.validate doesn't handle TLSv1.3
                Collections.singleton("TLSv1.2"));
        clientSSLContext.createSSLContext();

        SSLContextValidatorEngine.validate(clientSSLContext::createSSLEngine, serverSSLContext::createSSLEngine);
    }
}
