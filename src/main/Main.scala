package main

import syntax.Operation
import parser.Parser
import parser.PFail
import parser.PSucces
import handling.Handler

object Interpreter {
	

	def handleOperation(op: Operation): (Boolean, String) = {
		var message = "";
		var successfull = true;
		try {
			Handler.handle(op);
		} catch {
			case e: Exception =>
				message = Writer.errorWriter(e);
				successfull = false;
			case _: Throwable =>
				message = "We deserve to fail...";
				successfull = false;
		}
		if (successfull) {
			val message = Writer.successWriter(op);
		}
		return (successfull, message);
	}

	def indent(input:String) : String = {
		//input.split('\n');
		var output = "";
		input.trim().split('\n').map(line => s"\t$line").foreach(line => output = output + line);
		return output;
	}
	
	def interpret(string: String): List[(Boolean,String)] = {
		val commands = string.split(';');
		def parseCommands(cmds: Traversable[String]) : (List[(Boolean,String)],List[Operation]) = {
			if (cmds.size < 1)
				return (List[(Boolean,String)](),List[Operation]());
			val command = cmds.head;
			val parseResult = Parser.parse(command);
			parseResult match {
				case PSucces(operation) =>
					val tCommand = indent(command);
					val message = s"Successfully parsed command:\n$tCommand";
					val tail = parseCommands(cmds.tail)
					return ((false,message)::tail._1,operation::tail._2);
				case PFail(errorMessage) =>
					val tCommand = indent(command);
					val tErrorMessage = indent(errorMessage);
					val message = s"Error in command:\n $tCommand\nParsing error:\n$tErrorMessage";
					return (List[(Boolean,String)]((true,message)), List[Operation]());
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
		if (!success)
			return messages;
		else
			return handleOperations(operations);
	}
}

object Main {
	def main(args: Array[String]): Unit = {

	}
}