/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sk.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

import sk.util.vector.Vector2f;

/**
 *
 * Holds a 2-tuple vector.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */

public class Vector2f extends Vector implements Serializable, ReadableVector2f, WritableVector2f {

	private static final long serialVersionUID = 1L;

	public float x, y;

	/**
	 * Constructor for Vector2f.
	 */
	public Vector2f() {
		super();
	}

	/**
	 * Constructor.
	 */
	public Vector2f(ReadableVector2f src) {
		set(src);
	}

	/**
	 * Constructor.
	 */
	public Vector2f(float x, float y) {
		set(x, y);
	}

	public Vector2f(float[] data) {
		if (data.length < 2) {
			throw new IllegalArgumentException("Data supplied to Vector2f is not enought to create a vector");
		}
		this.x = data[0];
		this.y = data[1];
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector2f#set(float, float)
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Load from another Vector2f
	 * @param src The source vector
	 * @return this
	 */
	public Vector2f set(ReadableVector2f src) {
		x = src.getX();
		y = src.getY();
		return this;
	}

	/**
	 * @return the length squared of the vector
	 */
	public float lengthSquared() {
		return x * x + y * y;
	}

	/**
	 * Translate a vector
	 * @param x The translation in x
	 * @param y the translation in y
	 * @return this
	 */
	public Vector2f translate(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Negate a vector
	 * @return this
	 */
	public Vector2f negate() {
		x = -x;
		y = -y;
		return this;
	}

	/**
	 * Negate a vector and place the result in a destination vector.
	 * @param dest The destination vector or null if a new vector is to be created
	 * @return the negated vector
	 */
	public Vector2f negate(Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = -x;
		dest.y = -y;
		return dest;
	}


	/**
	 * Normalise this vector and place the result in another vector.
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the normalised vector
	 */
	public Vector2f normalise(Vector2f dest) {
		float l = length();

		if (dest == null)
			dest = new Vector2f(x / l, y / l);
		else
			dest.set(x / l, y / l);

		return dest;
	}

	/**
	 * The dot product of two vectors is calculated as
	 * v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @return left dot right
	 */
	public static float dot(Vector2f left, Vector2f right) {
		return left.x * right.x + left.y * right.y;
	}

	/**
	 * Adds vector b to this vector.
	 * Note that this is not a "safe" function 
	 * @param other The vector to be added
	 * @return itself after the addition
	 */
	public Vector2f add(Vector2f other) {
		x += other.x;
		y += other.y;
		return this;
	}
	
	/**
	 * Rotates a vector by the specified angle in radians.
	 * @param in The vector to rotate
	 * @param angle The angle to rotate the in vector by
	 * @param dest The vector to store the result in
	 * @return 
	 */
	public static Vector2f rotate(Vector2f in, float angle, Vector2f dest) {
		if (dest == null) {
			dest = new Vector2f();
		}
		
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		dest.x = in.x * c - in.y * s;
		dest.y = in.x * s + in.y * c;			
		return dest;
	}
	
	/**
	 * Rotates a vector by the specified angle in radians.
	 * @param angle the angle you want to rotate.
	 * @return the rotated version of itself.
	 */
	public Vector2f rotate(float angle) {
		Vector2f out = rotate(this, angle, null);
		this.x = out.x;
		this.y = out.y;
		return this;
	}

	/**
	 * Subtracts the supplied vector from itself
	 * @param other The Vector you want to subtract
	 * @return Itself after the subtraction
	 */
	public Vector2f sub(Vector2f other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	/**
	 * Returns a safe copy of itself
	 */
	public Vector2f clone() {
		return new Vector2f(x, y);
	}
	
	

	/**
	 * Calculate the angle between two vectors, in radians
	 * @param a A vector
	 * @param b The other vector
	 * @return the angle between the two vectors, in radians
	 */
	public static float angle(Vector2f a, Vector2f b) {
		float dls = dot(a, b) / (a.length() * b.length());
		if (dls < -1f)
			dls = -1f;
		else if (dls > 1.0f)
			dls = 1.0f;
		return (float)Math.acos(dls);
	}

	/**
	 * Add a vector to another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the sum of left and right in dest
	 */
	public static Vector2f add(Vector2f left, Vector2f right, Vector2f dest) {
		if (dest == null)
			return new Vector2f(left.x + right.x, left.y + right.y);
		else {
			dest.set(left.x + right.x, left.y + right.y);
			return dest;
		}
	}

	/**
	 * Subtract a vector from another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return left minus right in dest
	 */
	public static Vector2f sub(Vector2f left, Vector2f right, Vector2f dest) {
		if (dest == null)
			return new Vector2f(left.x - right.x, left.y - right.y);
		else {
			dest.set(left.x - right.x, left.y - right.y);
			return dest;
		}
	}

	/**
	 * Store this vector in a FloatBuffer
	 * @param buf The buffer to store it in, at the current position
	 * @return this
	 */
	public Vector store(FloatBuffer buf) {
		buf.put(x);
		buf.put(y);
		return this;
	}

	/**
	 * Load this vector from a FloatBuffer
	 * @param buf The buffer to load it from, at the current position
	 * @return this
	 */
	public Vector load(FloatBuffer buf) {
		x = buf.get();
		y = buf.get();
		return this;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Vector#scale(float)
	 */
	public Vector2f scale(float scale) {

		x *= scale;
		y *= scale;

		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("Vector2f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return x
	 */
	public final float getX() {
		return x;
	}

	/**
	 * @return y
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Set X
	 * @param x
	 */
	public final void setX(float x) {
		this.x = x;
	}

	/**
	 * Set Y
	 * @param y
	 */
	public final void setY(float y) {
		this.y = y;
	}	
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vector2f other = (Vector2f)obj;
		
		if (x == other.x && y == other.y) return true;
		
		return false;
	}
	
	public float[] getData() {
		return new float[] {x, y};
	}
	
	public float get(int i) {
		switch(i) {
		case 0: return x;
		case 1: return y;
		default: return -1;
		}
	}
	
	public int numValues() {
		return 2;
	}

	public float dot(Vector2f other) {
		return this.x * other.x + this.y * other.y;
	}


}
