<TeXmacs|1.99.12>

<style|generic>

<\body>
  <\equation*>
    m*<frac|\<mathd\>v|\<mathd\>t>=q*E-m*\<nu\>*<around*|(|v-u|)>
  </equation*>

  <\equation*>
    \<sigma\>=<frac|n*q<rsup|2>|m*\<nu\>>=n*q*\<mu\><application-space|1em>\<rightarrow\><application-space|1em>\<mu\>=<frac|q|m*\<nu\>><application-space|1em>\<rightarrow\><application-space|1em>m*\<nu\>=<frac|q|\<mu\>>
  </equation*>

  \;

  <\equation*>
    m*<frac|\<mathd\>v|\<mathd\>t>=q*<around*|(|E-<frac|v-u|\<mu\>>|)>
  </equation*>

  <\equation*>
    <frac|\<mathd\>v|\<mathd\>t>=a*v+b
  </equation*>

  <\equation*>
    a=-<frac|q|m*\<mu\>>
  </equation*>

  <\equation*>
    b=<frac|q|m>*<around*|(|E+<frac|u|\<mu\>>|)>
  </equation*>

  The solution is

  <\equation*>
    v<around*|(|t|)>=C*exp<around*|(|a*t|)>-<frac|b|a>
  </equation*>

  <\equation*>
    v<around*|(|t|)>=C*exp<around*|(|-<frac|q|m*\<mu\>>*t|)>-<frac|<frac|q|m>*<around*|(|E+<frac|u|\<mu\>>|)>|-<frac|q|m*\<mu\>>>=C*exp<around*|(|-<frac|t|\<tau\><rsub|c>>|)>+\<mu\>*E+u
  </equation*>

  where

  <\equation*>
    \<tau\><rsub|c>=<frac|m*\<mu\>|q>
  </equation*>

  The initial condition is

  <\equation*>
    v<around*|(|0|)>=C*+\<mu\>*E+u
  </equation*>

  <\equation*>
    C=v<around*|(|0|)>-<around*|(|\<mu\>*E+u|)>
  </equation*>

  <\equation*>
    v<around*|(|t|)>=v<around*|(|0|)>*exp<around*|(|-<frac|t|\<tau\><rsub|c>>|)>+<around*|(|\<mu\>*E+u|)><around*|(|1-exp<around*|(|-<frac|t|\<tau\><rsub|c>>|)>|)>
  </equation*>

  <\equation*>
    v<around*|(|t+\<mathd\>t|)>=v<around*|(|t|)>*exp<around*|(|-<frac|\<mathd\>t|\<tau\><rsub|c>>|)>+<around*|(|\<mu\>*E+u|)><around*|(|1-exp<around*|(|-<frac|\<mathd\>t|\<tau\><rsub|c>>|)>|)>
  </equation*>

  The original timestep implemented by <with|font-shape|italic|Dragion> was
  the <math|t\<rightarrow\>\<infty\>> limit,

  <\equation*>
    v<around*|(|t|)>=\<mu\>*E+u
  </equation*>

  \;
</body>

<\initial>
  <\collection>
    <associate|page-height|auto>
    <associate|page-type|letter>
    <associate|page-width|auto>
  </collection>
</initial>