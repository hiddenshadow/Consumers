import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Driver {

    private static final String HOST = "localhost";
    private static final String EXCHANGE_NAME = "commonExchange";
    private static final String EXCHANGE_TYPE = "direct";
    private static final boolean IS_DURABLE_EXCHANGE = true;
    private static final String MAIL_QUEUE = "mailQueue";
    private static final String ROUTING_KEY = "mail";


    public static void main(String[] args){

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, IS_DURABLE_EXCHANGE);
            String queueName = MAIL_QUEUE;

            channel.queueDeclare(queueName, true, false, false, null);

            String bindingKey = ROUTING_KEY;

            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
