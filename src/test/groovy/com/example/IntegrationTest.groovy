package com.example

import groovy.sql.Sql
import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.jms.core.JmsTemplate
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 8/5/13
 * Time: 11:06 AM
 */
class IntegrationTest extends Specification {
    @Shared def application = new Application()
    @Shared def jms = new JmsTemplate().with {
        connectionFactory = new ActiveMQConnectionFactory(JmsAccessor.BROKER_URI)
        receiveTimeout = 2000
        return it
    }
    @Shared def sql = Sql.newInstance(JdbcAccessor.JDBC_URL)

    def setupSpec() {
        application.start()
    }

    def cleanupSpec() {
        application.stop()
        sql.close()
    }

    def 'sanity check of overall system for first prime'() {
        when:
        jms.convertAndSend('NumberInput', '2')

        then:
        jms.receiveAndConvert('NumberOutput') == '3'
    }

    def 'sanity check of overall system for first non-prime'() {
        when:
        jms.convertAndSend('NumberInput', '4')

        then:
        jms.receiveAndConvert('NumberOutput') == '5'
    }

    def 'sanity check of database storage'() {
        when:
        jms.convertAndSend('NumberInput', '2')

        then:
        jms.receiveAndConvert('NumberOutput') // wait for processing
        sql.rows('select the_number from numbers').collect{ it.the_number }.contains(2L)
    }
    
    def 'check for value 2'(){ // check if the no is 2
	when:
	jms.convertAndSend('NumberInput', '2')
	
	then:
	jms.receiveAndConvert('NumberOutput') == '3'   

    
    }
    
    def 'check for value 0'(){ // check the zero conditoin for prime no logic
    	when:
    	jms.convertAndSend('NumberInput', '0')
    	
    	then:
    	jms.receiveAndConvert('NumberOutput') == '2'   
    
        
    }
    
    def 'check for value 1'(){ //testing the changed prime no logic to handle 1
        	when:
        	jms.convertAndSend('NumberInput', '1')
        	
        	then:
        	jms.receiveAndConvert('NumberOutput') == '2'   
        
            
    }
    
    def 'check for overall any prime'(){ // testing  prime no logic to handle any other number
        	when:
        	jms.convertAndSend('NumberInput', '11')
        	
        	then:
        	jms.receiveAndConvert('NumberOutput') == '12'   
        
            
    }
    
    def 'check for overall any non prime'(){ // testing  prime no logic to handle any other number
            	when:
            	jms.convertAndSend('NumberInput', '12')
            	
            	then:
            	jms.receiveAndConvert('NumberOutput') == '13'   
            
                
    }
    
   def 'performanceTest'() {
             
                 when:
                 long start = System.currentTimeMillis()
                 
                 for ( i in 1..100 ) {   
                 	def num = Math.random() * 999999 as int 		
                 	jms.convertAndSend('NumberInput', num)
         		}
         		for ( i in 1..100 ) {    		
                 	println "result:" + jms.receiveAndConvert('NumberOutput')
         		}
          
                 long timeelapsed = System.currentTimeMillis() - start
         		println "Time Elapsed:" + timeelapsed
         
                 then:
                 timeelapsed <= 2000
    }
 
}