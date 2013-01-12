package main

import syntax.Operation
import parser.Parser
import parser.PFail
import parser.PSucces
import handling.Handler

object Interpreter {
	def handleOperation(op: Operation): (Boolean, String) = {
		try {
			Handler.handle(op);
			return (true, Writer.successWriter(op));
		} catch {
			case e: Exception =>
				return (false, Writer.errorWriter(e)+"\n" + Writer.indent("For operation: " + op.toString));
			case _: Throwable =>
				return (false,"We deserve to fail...");
		}
	}
	
	def interpret(string: String): List[(Boolean,String)] = {
		val commands = string.split(';');
		
		def parseCommands(cmds: Traversable[String]) : (List[(Boolean,String)],List[Operation]) = {
			if (cmds.size < 1)
				return (List[(Boolean,String)](),List[Operation]());
			val command = cmds.head;
			if (command.trim() == "") {
				val tail = parseCommands(cmds.tail)
				return parseCommands(cmds.tail);
			} else {
				val parseResult = Parser.parse(command);
				parseResult match {
					case PSucces(operation) =>
						val tCommand = Writer.indent(command)
						val tOperation = Writer.indent(operation.toString)
						val message = s"Successfully parsed command:\n$tCommand\nto\n$tOperation";
						val tail = parseCommands(cmds.tail)
						return ((false,message)::tail._1,operation::tail._2);
					case PFail(errorMessage) =>
						val tCommand = Writer.indent(command);
						val tErrorMessage = Writer.indent(errorMessage);
						val message = s"Error in command:\n $tCommand\nParsing error:\n$tErrorMessage";
	
						val tail = parseCommands(cmds.tail)
						return ((true,message)::tail._1,tail._2);
						//return (List[(Boolean,String)]((true,message)), List[Operation]());
				}
			}
		}
		def handleOperations(ops: List[Operation]): List[(Boolean,String)] = {
			if (ops.size < 1)
				return List[(Boolean,String)]();
			
			val (successfull, message) = handleOperation(ops.head)
			if (successfull)
				return (false,message)::handleOperations(ops.tail);
			else
				return List[(Boolean,String)]((true,message));
		}
		val (messages,operations) = parseCommands(commands);
		val success = messages.foldRight[Boolean](true)((e,s) => s && !e._1);
		val parsing = """

=========================================================
=========================================================
                        PARSING
=========================================================
=========================================================
				"""
		val outputString =(false,parsing)::messages 
		if (!success) {
			return outputString;
		}
		else {
			val executing = """
=========================================================
=========================================================
                      EXECUTING
=========================================================			
=========================================================
				"""	//+ operations.toString;
			return outputString ++ ((false,executing) :: handleOperations(operations));
		}
	}
}