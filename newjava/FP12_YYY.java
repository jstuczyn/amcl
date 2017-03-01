/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

/* AMCL Fp^12 functions */
/* FP12 elements are of the form a+i.b+i^2.c */

package amcl;

public final class FP12_YYY {
	private final FP4_YYY a;
	private final FP4_YYY b;
	private final FP4_YYY c;
/* reduce all components of this mod Modulus */
	public void reduce()
	{
		a.reduce();
		b.reduce();
		c.reduce();
	}
/* normalise all components of this */
	public void norm()
	{
		a.norm();
		b.norm();
		c.norm();
	}
/* test x==0 ? */
	public boolean iszilch() {
		reduce();
		return (a.iszilch() && b.iszilch() && c.iszilch());
	}
/* test x==1 ? */
	public boolean isunity() {
		FP4_YYY one=new FP4_YYY(1);
		return (a.equals(one) && b.iszilch() && c.iszilch());
	}
/* return 1 if x==y, else 0 */
	public boolean equals(FP12_YYY x)
	{
		return (a.equals(x.a) && b.equals(x.b) && c.equals(x.c));
	}
/* extract a from this */
	public FP4_YYY geta()
	{
		return a;
	}
/* extract b */
	public FP4_YYY getb()
	{
		return b;
	}
/* extract c */
	public FP4_YYY getc()
	{
		return c;
	}
/* copy this=x */
	public void copy(FP12_YYY x)
	{
		a.copy(x.a);
		b.copy(x.b);
		c.copy(x.c);
	}
/* set this=1 */
	public void one()
	{
		a.one();
		b.zero();
		c.zero();
	}
/* this=conj(this) */
	public void conj()
	{
		a.conj();
		b.nconj();
		c.conj();
	}
/* Constructors */
	public FP12_YYY(FP4_YYY d)
	{
		a=new FP4_YYY(d);
		b=new FP4_YYY(0);
		c=new FP4_YYY(0);
	}

	public FP12_YYY(int d)
	{
		a=new FP4_YYY(d);
		b=new FP4_YYY(0);
		c=new FP4_YYY(0);
	}

	public FP12_YYY(FP4_YYY d,FP4_YYY e,FP4_YYY f)
	{
		a=new FP4_YYY(d);
		b=new FP4_YYY(e);
		c=new FP4_YYY(f);
	}

	public FP12_YYY(FP12_YYY x)
	{
		a=new FP4_YYY(x.a);
		b=new FP4_YYY(x.b);
		c=new FP4_YYY(x.c);
	}

/* Granger-Scott Unitary Squaring */
	public void usqr()
	{
		FP4_YYY A=new FP4_YYY(a);
		FP4_YYY B=new FP4_YYY(c);
		FP4_YYY C=new FP4_YYY(b);
		FP4_YYY D=new FP4_YYY(0);

		a.sqr();
		D.copy(a); D.add(a);
		a.add(D);

		a.norm();
		A.nconj();

		A.add(A);
		a.add(A);
		B.sqr();
		B.times_i();

		D.copy(B); D.add(B);
		B.add(D);
		B.norm();

		C.sqr();
		D.copy(C); D.add(C);
		C.add(D);
		C.norm();

		b.conj();
		b.add(b);
		c.nconj();

		c.add(c);
		b.add(B);
		c.add(C);
		reduce();

	}

/* Chung-Hasan SQR2 method from http://cacr.uwaterloo.ca/techreports/2006/cacr2006-24.pdf */
	public void sqr()
	{
		FP4_YYY A=new FP4_YYY(a);
		FP4_YYY B=new FP4_YYY(b);
		FP4_YYY C=new FP4_YYY(c);
		FP4_YYY D=new FP4_YYY(a);

		A.sqr();
		B.mul(c);
		B.add(B);
		C.sqr();
		D.mul(b);
		D.add(D);

		c.add(a);
		c.add(b);
		c.sqr();

		a.copy(A);

		A.add(B);
		A.norm();
		A.add(C);
		A.add(D);
		A.norm();

		A.neg();
		B.times_i();
		C.times_i();

		a.add(B);

		b.copy(C); b.add(D);
		c.add(A);
		norm();
	}

/* FP12 full multiplication this=this*y */
	public void mul(FP12_YYY y)
	{
		FP4_YYY z0=new FP4_YYY(a);
		FP4_YYY z1=new FP4_YYY(0);
		FP4_YYY z2=new FP4_YYY(b);
		FP4_YYY z3=new FP4_YYY(0);
		FP4_YYY t0=new FP4_YYY(a);
		FP4_YYY t1=new FP4_YYY(y.a);

		z0.mul(y.a);
		z2.mul(y.b);

		t0.add(b);
		t1.add(y.b);

		z1.copy(t0); z1.mul(t1);
		t0.copy(b); t0.add(c);

		t1.copy(y.b); t1.add(y.c);
		z3.copy(t0); z3.mul(t1);

		t0.copy(z0); t0.neg();
		t1.copy(z2); t1.neg();

		z1.add(t0);
		z1.norm();
		b.copy(z1); b.add(t1);

		z3.add(t1);
		z2.add(t0);

		t0.copy(a); t0.add(c);
		t1.copy(y.a); t1.add(y.c);
		t0.mul(t1);
		z2.add(t0);

		t0.copy(c); t0.mul(y.c);
		t1.copy(t0); t1.neg();

		z2.norm();
		z3.norm();
		b.norm();

		c.copy(z2); c.add(t1);
		z3.add(t1);
		t0.times_i();
		b.add(t0);

		z3.times_i();
		a.copy(z0); a.add(z3);
		norm();
	}

/* Special case of multiplication arises from special form of ATE pairing line function */
	public void smul(FP12_YYY y)
	{
		FP4_YYY z0=new FP4_YYY(a);
		FP4_YYY z2=new FP4_YYY(b);
		FP4_YYY z3=new FP4_YYY(b);
		FP4_YYY t0=new FP4_YYY(0);
		FP4_YYY t1=new FP4_YYY(y.a);
		
		z0.mul(y.a);
		z2.pmul(y.b.real());
		b.add(a);
		t1.real().add(y.b.real());

		b.mul(t1);
		z3.add(c);
		z3.pmul(y.b.real());

		t0.copy(z0); t0.neg();
		t1.copy(z2); t1.neg();

		b.add(t0);
		b.norm();

		b.add(t1);
		z3.add(t1);
		z2.add(t0);

		t0.copy(a); t0.add(c);
		t0.mul(y.a);
		c.copy(z2); c.add(t0);

		z3.times_i();
		a.copy(z0); a.add(z3);

		norm();
	}

/* this=1/this */
	public void inverse()
	{
		FP4_YYY f0=new FP4_YYY(a);
		FP4_YYY f1=new FP4_YYY(b);
		FP4_YYY f2=new FP4_YYY(a);
		FP4_YYY f3=new FP4_YYY(0);

		norm();
		f0.sqr();
		f1.mul(c);
		f1.times_i();
		f0.sub(f1);

		f1.copy(c); f1.sqr();
		f1.times_i();
		f2.mul(b);
		f1.sub(f2);

		f2.copy(b); f2.sqr();
		f3.copy(a); f3.mul(c);
		f2.sub(f3);

		f3.copy(b); f3.mul(f2);
		f3.times_i();
		a.mul(f0);
		f3.add(a);
		c.mul(f1);
		c.times_i();

		f3.add(c);
		f3.inverse();
		a.copy(f0); a.mul(f3);
		b.copy(f1); b.mul(f3);
		c.copy(f2); c.mul(f3);
	}

/* this=this^p using Frobenius */
	public void frob(FP2_YYY f)
	{
		FP2_YYY f2=new FP2_YYY(f);
		FP2_YYY f3=new FP2_YYY(f);

		f2.sqr();
		f3.mul(f2);

		a.frob(f3);
		b.frob(f3);
		c.frob(f3);

		b.pmul(f);
		c.pmul(f2);
	}

/* trace function */
	public FP4_YYY trace()
	{
		FP4_YYY t=new FP4_YYY(0);
		t.copy(a);
		t.imul(3);
		t.reduce();
		return t;
	}

/* convert from byte array to FP12 */
	public static FP12_YYY fromBytes(byte[] w)
	{
		BIG_XXX a,b;
		FP2_YYY c,d;
		FP4_YYY e,f,g;
		byte[] t=new byte[BIG_XXX.MODBYTES];

		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		c=new FP2_YYY(a,b);

		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+2*BIG_XXX.MODBYTES];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+3*BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		d=new FP2_YYY(a,b);

		e=new FP4_YYY(c,d);


		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+4*BIG_XXX.MODBYTES];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+5*BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		c=new FP2_YYY(a,b);

		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+6*BIG_XXX.MODBYTES];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+7*BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		d=new FP2_YYY(a,b);

		f=new FP4_YYY(c,d);


		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+8*BIG_XXX.MODBYTES];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+9*BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		c=new FP2_YYY(a,b);

		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+10*BIG_XXX.MODBYTES];
		a=BIG_XXX.fromBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) t[i]=w[i+11*BIG_XXX.MODBYTES];
		b=BIG_XXX.fromBytes(t);
		d=new FP2_YYY(a,b);

		g=new FP4_YYY(c,d);

		return new FP12_YYY(e,f,g);
	}

/* convert this to byte array */
	public void toBytes(byte[] w)
	{
		byte[] t=new byte[BIG_XXX.MODBYTES];
		a.geta().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i]=t[i];
		a.geta().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+BIG_XXX.MODBYTES]=t[i];
		a.getb().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+2*BIG_XXX.MODBYTES]=t[i];
		a.getb().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+3*BIG_XXX.MODBYTES]=t[i];

		b.geta().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+4*BIG_XXX.MODBYTES]=t[i];
		b.geta().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+5*BIG_XXX.MODBYTES]=t[i];
		b.getb().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+6*BIG_XXX.MODBYTES]=t[i];
		b.getb().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+7*BIG_XXX.MODBYTES]=t[i];

		c.geta().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+8*BIG_XXX.MODBYTES]=t[i];
		c.geta().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+9*BIG_XXX.MODBYTES]=t[i];
		c.getb().getA().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+10*BIG_XXX.MODBYTES]=t[i];
		c.getb().getB().toBytes(t);
		for (int i=0;i<BIG_XXX.MODBYTES;i++) w[i+11*BIG_XXX.MODBYTES]=t[i];
	}

/* convert to hex string */
	public String toString() 
	{
		return ("["+a.toString()+","+b.toString()+","+c.toString()+"]");
	}

/* this=this^e */ 
/* Note this is simple square and multiply, so not side-channel safe */
	public FP12_YYY pow(BIG_XXX e)
	{
		norm();
		e.norm();
		FP12_YYY w=new FP12_YYY(this);
		BIG_XXX z=new BIG_XXX(e);
		FP12_YYY r=new FP12_YYY(1);

		while (true)
		{
			int bt=z.parity();
			z.fshr(1);
			if (bt==1) r.mul(w);
			if (z.iszilch()) break;
			w.usqr();
		}
		r.reduce();
		return r;
	}

/* constant time powering by small integer of max length bts */
	public void pinpow(int e,int bts)
	{
		int i,b;
		FP12_YYY [] R=new FP12_YYY[2];
		R[0]=new FP12_YYY(1);
		R[1]=new FP12_YYY(this);
		for (i=bts-1;i>=0;i--)
		{
			b=(e>>i)&1;
			R[1-b].mul(R[b]);
			R[b].usqr();
		}
		this.copy(R[0]);
	}

/* p=q0^u0.q1^u1.q2^u2.q3^u3 */
/* Timing attack secure, but not cache attack secure */

	public static FP12_YYY pow4(FP12_YYY[] q,BIG_XXX[] u)
	{
		int i,j,nb,m;
		int[] a=new int[4];
		FP12_YYY [] g=new FP12_YYY[8];
		FP12_YYY [] s=new FP12_YYY[2];
		FP12_YYY c=new FP12_YYY(1);
		FP12_YYY p=new FP12_YYY(0);
		BIG_XXX [] t=new BIG_XXX[4];
		BIG_XXX mt=new BIG_XXX(0);
		byte[] w=new byte[BIG_XXX.NLEN*BIG_XXX.BASEBITS+1];

		for (i=0;i<4;i++)
			t[i]=new BIG_XXX(u[i]);

		s[0]=new FP12_YYY(0);
		s[1]=new FP12_YYY(0);

		g[0]=new FP12_YYY(q[0]); s[0].copy(q[1]); s[0].conj(); g[0].mul(s[0]);
		g[1]=new FP12_YYY(g[0]);
		g[2]=new FP12_YYY(g[0]);
		g[3]=new FP12_YYY(g[0]);
		g[4]=new FP12_YYY(q[0]); g[4].mul(q[1]);
		g[5]=new FP12_YYY(g[4]);
		g[6]=new FP12_YYY(g[4]);
		g[7]=new FP12_YYY(g[4]);

		s[1].copy(q[2]); s[0].copy(q[3]); s[0].conj(); s[1].mul(s[0]);
		s[0].copy(s[1]); s[0].conj(); g[1].mul(s[0]);
		g[2].mul(s[1]);
		g[5].mul(s[0]);
		g[6].mul(s[1]);
		s[1].copy(q[2]); s[1].mul(q[3]);
		s[0].copy(s[1]); s[0].conj(); g[0].mul(s[0]);
		g[3].mul(s[1]);
		g[4].mul(s[0]);
		g[7].mul(s[1]);

/* if power is even add 1 to power, and add q to correction */

		for (i=0;i<4;i++)
		{
			if (t[i].parity()==0)
			{
				t[i].inc(1); t[i].norm();
				c.mul(q[i]);
			}
			mt.add(t[i]); mt.norm();
		}
		c.conj();
		nb=1+mt.nbits();

/* convert exponent to signed 1-bit window */
		for (j=0;j<nb;j++)
		{
			for (i=0;i<4;i++)
			{
				a[i]=(t[i].lastbits(2)-2);
				t[i].dec(a[i]); t[i].norm(); 
				t[i].fshr(1);
			}
			w[j]=(byte)(8*a[0]+4*a[1]+2*a[2]+a[3]);
		}
		w[nb]=(byte)(8*t[0].lastbits(2)+4*t[1].lastbits(2)+2*t[2].lastbits(2)+t[3].lastbits(2));
		p.copy(g[(w[nb]-1)/2]);  

		for (i=nb-1;i>=0;i--)
		{
			m=w[i]>>7;
			j=(w[i]^m)-m;  /* j=abs(w[i]) */
			j=(j-1)/2;
			s[0].copy(g[j]); s[1].copy(g[j]); s[1].conj();
			p.usqr();
			p.mul(s[m&1]);
		}
		p.mul(c);  /* apply correction */
		p.reduce();
		return p;
	}

/*
	public static void main(String[] args) {
		BIG_XXX p=new BIG_XXX(ROM_YYY.Modulus);
		FP2_YYY w0,w1;
		BIG_XXX a=new BIG_XXX(0);
		BIG_XXX b=new BIG_XXX(0);
		
		a.zero(); b.zero(); a.inc(1); b.inc(2);
		w0=new FP2_YYY(a,b);
		a.zero(); b.zero(); a.inc(3); b.inc(4);
		w1=new FP2_YYY(a,b);
		FP4_YYY t0=new FP4_YYY(w0,w1);

		a.zero(); b.zero(); a.inc(5); b.inc(6);
		w0=new FP2_YYY(a,b);
		a.zero(); b.zero(); a.inc(7); b.inc(8);
		w1=new FP2_YYY(a,b);
		FP4_YYY t1=new FP4_YYY(w0,w1);

		a.zero(); b.zero(); a.inc(9); b.inc(10);
		w0=new FP2_YYY(a,b);
		a.zero(); b.zero(); a.inc(11); b.inc(12);
		w1=new FP2_YYY(a,b);
		FP4_YYY t2=new FP4_YYY(w0,w1);

		FP12_YYY w=new FP12_YYY(t0,t1,t2);
		FP12_YYY t=new FP12_YYY(w);

		System.out.println("w= "+w.toString());

		a=new BIG_XXX(ROM_ZZZ.CURVE_Fra);
		b=new BIG_XXX(ROM_ZZZ.CURVE_Frb);

		FP2_YYY f=new FP2_YYY(a,b);

		w.frob(f);
		System.out.println("w= "+w.toString());

		w=t.pow(p);

		System.out.println("w= "+w.toString());

		w.inverse();

		System.out.println("1/w= "+w.toString());

		w.inverse();

		System.out.println("w= "+w.toString());

		t.copy(w);
		w.conj();
		t.inverse();
		w.mul(t);

		System.out.println("w^(p^6-1)= "+w.toString());

		t.copy(w);
		w.frob(f);
		w.frob(f);
		w.mul(t);

		System.out.println("w^(p^6-1)(p^2+1)= "+w.toString());

		t.copy(w);

		t.inverse();
		w.conj();

		System.out.println("w= "+w.toString());
		System.out.println("t= "+t.toString());
	} */
}