

/**
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
 */

package io.streamnative.pulsar.handlers.amqp.rabbitmq.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.test.BrokerTestCase;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
/**
 * Testcase.
 */
public class BasicGet extends BrokerTestCase {
    //@Test
    public void basicGetWithEnqueuedMessages() throws IOException, InterruptedException {
        assertTrue(channel.isOpen());
        String q = channel.queueDeclare().getQueue();

        basicPublishPersistent("msg".getBytes(StandardCharsets.UTF_8), q);
        Thread.sleep(250);

        assertNotNull(channel.basicGet(q, true));
        channel.queuePurge(q);
        assertNull(channel.basicGet(q, true));
        channel.queueDelete(q);
    }

    //@Test
    public void basicGetWithEmptyQueue() throws IOException, InterruptedException {
        assertTrue(channel.isOpen());
        String q = channel.queueDeclare().getQueue();

        assertNull(channel.basicGet(q, true));
        channel.queueDelete(q);
    }

    //@Test
    public void basicGetWithClosedChannel() throws IOException, InterruptedException, TimeoutException {
        assertTrue(channel.isOpen());
        String q = channel.queueDeclare().getQueue();

        channel.close();
        assertFalse(channel.isOpen());
        try {
            channel.basicGet(q, true);
            fail("expected basic.get on a closed channel to fail");
        } catch (AlreadyClosedException e) {
            // passed
        } finally {
            Channel tch = connection.createChannel();
            tch.queueDelete(q);
            tch.close();
        }

    }
}
