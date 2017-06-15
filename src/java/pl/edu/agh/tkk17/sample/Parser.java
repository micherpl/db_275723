package pl.edu.agh.tkk17.sample;

import java.util.Arrays;
import java.util.Iterator;

public class Parser
{
    private Iterator<Token> tokens;
    private Token ctoken;

    public Parser(Iterable<Token> tokens)
    {
        this.tokens = tokens.iterator();
        this.forward();
    }

    private void forward()
    {
        this.ctoken = this.tokens.next();
    }

    private String value()
    {
        return this.ctoken.getValue();
    }

    private boolean check(TokenType type)
    {
        return this.ctoken.getType() == type;
    }

    private void expect(TokenType type)
    {
        if (!this.check(type)) {
            throw new UnexpectedTokenException(this.ctoken, type);
        }
    }

    private Node parseNumber()
    {
        this.expect(TokenType.NUM);
        String value = this.value();
        this.forward();
        return new NodeNumber(value);
    }

    private Node parseTerm()
    {
		Node left = this.parseFactor();

        if (this.check(TokenType.MUL)) {
            this.forward();
            Node right = this.parseTerm();
            return new NodeMul(left, right);
        } else {
            return left;
        }
    }
	
	private Node parseFactor() 
	{
        Node left;
        if (check(TokenType.LN)) {
            forward();
            left = parseExpression();
            expect(TokenType.PN);
            forward();
        } else {
            left = parseNumber();
        }
        return left;
    }

    private Node parseExpression()
    {
        Node left = this.parseTerm();
        if (this.check(TokenType.ADD)) {
            this.forward();
            Node right = this.parseExpression();
            return new NodeAdd(left, right);
		} else if (check(TokenType.SUB)) {
            forward();
            Node right = parseExpression();
            return new NodeSub(left, right);
		 } else if (check(TokenType.DIV)) {
            forward();
            Node right = parseTerm();
            this.checkByZero(right);
            return new NodeDiv(left, right);			
        } else {
            return left;
        }
    }
	
	private void checkByZero(Node right) {
        if (right instanceof NodeNumber) {
            if (((NodeNumber) right).getValue().equals("0")) {
                throw new OutputableException("Division by 0.");
            }
        }
    }

    private Node parseProgram()
    {
        Node root = this.parseExpression();
        this.expect(TokenType.END);
        return root;
    }

    public static Node parse(Iterable<Token> tokens)
    {
        Parser parser = new Parser(tokens);
        Node root = parser.parseProgram();
        return root;
    }
}
