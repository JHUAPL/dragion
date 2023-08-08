<TeXmacs|1.99.12>

<style|generic>

<\body>
  <paragraph|Fundamental scales>

  <\equation*>
    R=object size
  </equation*>

  <with|color|pastel grey|<\equation*>
    L=domain size
  </equation*>>

  <\equation*>
    \<Phi\><rsub|0>=characteristic electric potential
  </equation*>

  <\equation*>
    \<mu\>=mobility
  </equation*>

  <\equation*>
    n=concentration
  </equation*>

  <\equation*>
    \<mathd\>t=timestep
  </equation*>

  5 quantities, 3 units [m,s,C] <math|\<longrightarrow\>> 2 ratios

  <paragraph|Dimensionless ratios>

  <with|color|pastel brown|<\equation*>
    \<frak-S\>=<frac|R|L>
  </equation*>>

  <\equation*>
    \<frak-M\>\<equiv\><frac|v*\<mathd\>t|R>=<frac|\<mu\>*E*\<mathd\>t|R>=<frac|\<mu\>*\<Phi\><rsub|0>\<cdot\>\<mathd\>t|R\<cdot\>R>=<frac|\<mu\>*\<Phi\><rsub|0>*\<mathd\>t|R<rsup|2>>
  </equation*>

  <\equation*>
    \<frak-N\>\<equiv\>n*R<rsup|3>
  </equation*>

  <paragraph|Conductivity>

  Adding fundamental charge scale <math|e>,

  <\equation*>
    \<space\>\<sigma\>=<frac|j|E>=<frac|n*e*v|E>=<frac|n*e\<cdot\>\<mu\>*E|E>=n*e*\<mu\>
  </equation*>

  <\equation*>
    \<frak-M\>=<frac|\<mu\>*\<Phi\><rsub|0>*\<mathd\>t|R<rsup|2>>=<frac|\<sigma\>*\<Phi\><rsub|0>*\<mathd\>t|n*e*R<rsup|2>>=<frac|\<sigma\>*\<Phi\><rsub|0>*\<mathd\>t|\<frak-N\>*e>*R
  </equation*>

  <\equation*>
    \<frak-M\>*\<frak-N\>=<frac|\<sigma\>*\<Phi\><rsub|0>*\<mathd\>t|*e>*R=<frac|\<sigma\>*Q<rsub|0>*\<mathd\>t|*4*\<pi\>*\<varepsilon\><rsub|0>*R*e>*R\<propto\>*<frac|\<sigma\>*\<mathd\>t|\<varepsilon\><rsub|0>>*<frac|Q<rsub|0>|e>=<frac|\<mathd\>t|\<tau\>>*<frac|Q<rsub|0>|e>
  </equation*>

  where <math|\<tau\>=<frac|\<varepsilon\><rsub|0>|\<sigma\>>>. Slope of
  relaxation probe trace is <math|slope=\<tau\><rsup|-1>=<frac|\<sigma\>|\<varepsilon\><rsub|0>>>.

  <paragraph|Alternative (independent) ratios>

  <\equation*>
    \<cal-T\>\<equiv\><frac|\<mathd\>t|\<tau\>>
  </equation*>

  <\equation*>
    \<cal-Q\>=<frac|Q<rsub|0>|e>
  </equation*>

  <\equation*>
    \<cal-N\>\<equiv\>n*R<rsup|3>
  </equation*>

  <paragraph|Timestep constraint>

  <\equation*>
    v*\<mathd\>t\<less\>R
  </equation*>

  <\equation*>
    \<mu\>*E*\<mathd\>t\<less\>R
  </equation*>

  <\equation*>
    <frac|\<mu\>*\<Phi\><rsub|0>|R>*\<mathd\>t\<less\>R
  </equation*>

  <\equation*>
    \<mathd\>t\<less\><frac|R<rsup|2>|\<mu\>*\<Phi\><rsub|0>>
  </equation*>

  <\equation*>
    <frac|\<mathd\>t|\<tau\>>\<less\><frac|R<rsup|2>|\<tau\>*\<mu\>*\<Phi\><rsub|0>>
  </equation*>

  <\equation*>
    <frac|\<mathd\>t|\<tau\>>\<less\><frac|\<sigma\>*R<rsup|2>|\<varepsilon\><rsub|0>*\<mu\>*\<Phi\><rsub|0>>
  </equation*>

  <\equation*>
    <frac|\<mathd\>t|\<tau\>>\<less\><frac|n*e*\<mu\>*R<rsup|2>|\<varepsilon\><rsub|0>*\<mu\>*\<Phi\><rsub|0>>
  </equation*>

  <\equation*>
    <frac|\<mathd\>t|\<tau\>>\<less\><frac|n*e*R<rsup|2>|\<varepsilon\><rsub|0>*\<Phi\><rsub|0>>
  </equation*>

  <\equation*>
    \<Phi\>=\<Phi\><rsub|0>*exp<around*|(|<frac|t|\<tau\>>|)>=\<Phi\><rsub|0>*exp<around*|(|<frac|i*\<mathd\>t|\<tau\>>|)>=\<Phi\><rsub|0>*exp<around*|(|i*<frac|n*e*R<rsup|2>|\<varepsilon\><rsub|0>*\<Phi\><rsub|0>>|)>
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

<\references>
  <\collection>
    <associate|auto-1|<tuple|1|?>>
    <associate|auto-2|<tuple|2|?>>
    <associate|auto-3|<tuple|3|?>>
    <associate|auto-4|<tuple|4|?>>
    <associate|auto-5|<tuple|5|?>>
  </collection>
</references>

<\auxiliary>
  <\collection>
    <\associate|toc>
      <with|par-left|<quote|4tab>|Fundamental scales
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-1><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Dimensionless ratios
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-2><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Conductivity
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-3><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Alternative (independent) ratios
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-4><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Timestep constraint
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-5><vspace|0.15fn>>
    </associate>
  </collection>
</auxiliary>