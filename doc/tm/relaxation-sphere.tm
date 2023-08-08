<TeXmacs|1.99.12>

<style|generic>

<\body>
  <paragraph|Isolated sphere>

  For a uniformly charged (and electrically isolated) sphere,

  <\equation*>
    E<rsub|r>=<frac|Q|4*\<pi\>*\<varepsilon\><rsub|0> r<rsup|2>>
  </equation*>

  where initially <math|Q=Q<rsub|0>>.

  <\equation*>
    <wide|v|\<vect\>>=\<mu\>*<wide|E|\<vect\>>
  </equation*>

  <\equation*>
    I=-n*q*<wide|v|\<vect\>><rsub|r>\<cdot\><wide|A|\<vect\>>=n*q*\<mu\>*E<rsub|r>\<cdot\>4*\<pi\>*R<rsup|2>
  </equation*>

  Note that <math|q=Z e> must have opposite sign of <math|E<rsub|r>> (and
  <math|Q<rsub|0>>) to be attracted.

  <\equation*>
    <wide|Q|\<dot\>>=n*q*\<mu\>*<frac|Q|4*\<pi\>*\<varepsilon\><rsub|0>
    R<rsup|2>>\<cdot\>4*\<pi\>*R<rsup|2>
  </equation*>

  If <math|n> is steady and uniform then

  <\equation*>
    Q=Q<rsub|0> exp<around*|(|-<frac|n <around*|\||q|\|>
    \<mu\>|\<varepsilon\><rsub|0>>*t|)>
  </equation*>

  Note that <math|\<sigma\>\<equiv\>n <around*|\||q|\|> \<mu\>> so that

  <\equation*>
    Q=Q<rsub|0> exp<around*|(|-<frac|\<sigma\>|\<varepsilon\><rsub|0>>*t|)>
  </equation*>

  \;

  <paragraph|With constant windspeed>

  The timestep equation is

  <\equation*>
    v<around*|(|t|)>=v<around*|(|0|)>*exp<around*|(|-<frac|\<mathd\>t|\<tau\><rsub|c>>|)>+<around*|(|\<mu\>*E+u|)><around*|(|1-exp<around*|(|-<frac|\<mathd\>t|\<tau\><rsub|c>>|)>|)>
  </equation*>

  and with <math|\<mathd\>t\<gg\>\<tau\><rsub|c>>,

  <\equation*>
    v<around*|(|t|)>=\<mu\>*E+u
  </equation*>

  Total current (positive = to the ball) is

  <\equation*>
    I=-<big|int>n*q*<wide|v|\<vect\>>\<cdot\>\<mathd\><wide|A|\<vect\>>=-<big|int>n*q*<around*|(|\<mu\>*<wide|E|\<vect\>>+<wide|u|\<vect\>>|)>\<cdot\>\<mathd\><wide|A|\<vect\>>
  </equation*>

  The electric field/mobility part is

  <\equation*>
    I<rsub|\<mu\>>=-<big|int>n*q*<around*|(|\<mu\>*<wide|E|\<vect\>>|)>\<cdot\>\<mathd\><wide|A|\<vect\>>=-n*q*<around*|(|4*\<pi\>*R<rsup|2>
    \<mu\>*E<rsub|r>|)>=-n*q\<cdot\>4*\<pi\>*R<rsup|2>
    \<mu\>\<cdot\><frac|Q|4*\<pi\>*\<varepsilon\><rsub|0>*R<rsup|2>>=-<frac|n*q*\<mu\>|\<varepsilon\><rsub|0>>*Q=-<frac|\<sigma\>|\<varepsilon\><rsub|0>>*Q
  </equation*>

  and the wind contribution is

  <\equation*>
    I<rsub|u>=-<big|int>n*q*<around*|(|<wide|u|\<vect\>>|)>\<cdot\>\<mathd\><wide|A|\<vect\>>=-n*q*v<rsub|wind><below|<big|int>|hemi>*<wide|x|^>\<cdot\>\<mathd\><wide|A|\<vect\>>*
  </equation*>

  <\equation*>
    <below|<big|int>|hemi>*<wide|x|^>\<cdot\>\<mathd\><wide|A|\<vect\>>*=R<rsup|2><below|<above|<big|int>|\<pi\>/2>|0>\<mathd\><around*|(|cos
    \<theta\>|)><below|<above|<big|int>|2*\<pi\>>|0>\<mathd\>\<phi\>
    <wide|x|^>\<cdot\><wide|r|^>=2*\<pi\>*R<rsup|2><below|<above|<big|int>|\<pi\>/2>|0>\<mathd\><around*|(|cos
    \<theta\>|)> cos \<theta\>=2*\<pi\>*R<rsup|2><around*|{|<frac|1|2>*cos<rsup|2>
    <frac|\<pi\>|2>-<frac|1|2>*cos<rsup|2> 0|}>=-\<pi\>*R<rsup|2>
  </equation*>

  <\equation*>
    I<rsub|u>=n*q*v<rsub|wind>*\<pi\>*R<rsup|2>
  </equation*>

  so the total current becomes

  <\equation*>
    I=-<frac|\<sigma\>|\<varepsilon\><rsub|0>>*Q+n*q
    v<rsub|wind>*\<pi\>*R<rsup|2>
  </equation*>

  where

  <\equation*>
    \<tau\><rsub|\<sigma\>>=<frac|\<varepsilon\><rsub|0>|\<sigma\>>
  </equation*>

  Grouping terms,

  <\equation*>
    <wide|Q|\<dot\>>=-<frac|Q|\<tau\><rsub|\<sigma\>>>+I<rsub|wind>
  </equation*>

  <\equation*>
    Q=C \<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*\<tau\><rsub|\<sigma\>>
  </equation*>

  here\ 

  <\equation*>
    Q<rsub|0>=C +I<rsub|wind>*\<tau\><rsub|\<sigma\>><space|1em>\<longrightarrow\>\<space\>\<space\><application-space|1em>C=Q<rsub|0>-I<rsub|wind>*\<tau\><rsub|\<sigma\>>
  </equation*>

  <\equation*>
    Q=Q<rsub|0> \<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  <paragraph|Wake/collection-area correction>

  <\with|color|pastel brown>
    There is an extra factor <math|w> on the <math|Q>-term when the windspeed
    is great (because of the wake),

    <\equation*>
      <wide|Q|\<dot\>>=-<frac|1|w>*<frac|Q|\<tau\><rsub|\<sigma\>>>+I<rsub|wind>
    </equation*>

    <\equation*>
      Q=C \<bbb-e\><rsup|-t/w*\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*w**\<tau\><rsub|\<sigma\>>
    </equation*>

    <\equation*>
      Q<rsub|0>=C +I<rsub|wind>*w**\<tau\><rsub|\<sigma\>><space|1em>\<longrightarrow\>\<space\>\<space\><application-space|1em>C=Q<rsub|0>-I<rsub|wind>*w**\<tau\><rsub|\<sigma\>>
    </equation*>

    <\equation*>
      Q=Q<rsub|0> \<bbb-e\><rsup|-t/w*\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*w**\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/w*\<tau\><rsub|\<sigma\>>>|)>
    </equation*>
  </with>

  The quasisteady velocity field is

  <\equation*>
    <wide|v|\<vect\>>=\<mu\>*<wide|E|\<vect\>>+<wide|u|\<vect\>>=\<mu\>*E*<wide|r|^>+<around*|\||u|\|>*<wide|u|^>
  </equation*>

  <\equation*>
    <wide|v|\<vect\>>\<cdot\><wide|r|^>=\<mu\>*E*<around*|(|<wide|r|^>\<cdot\><wide|r|^>|)>+<around*|\||u|\|>*<around*|(|<wide|u|^>\<cdot\><wide|r|^>|)>=\<mu\>*E-<around*|\||u|\|>*cos
    \<eta\>
  </equation*>

  where <math|cos \<eta\>\<equiv\>-<wide|u|^>\<cdot\><wide|r|^>>. The only
  particles at <math|r=R> that can reach the ball have
  <math|<wide|v|\<vect\>>\<cdot\><wide|r|^>\<less\>0>,

  <\equation*>
    \<mu\>*E-<around*|\||u|\|>*cos \<eta\>\<less\>0
  </equation*>

  <\equation*>
    cos \<eta\>\<less\><frac|\<mu\>*E|<around*|\||u|\|>>
  </equation*>

  Total current to the ball is

  <\equation*>
    I=-<below|<big|int>|<wide|v|\<vect\>>\<cdot\>\<mathd\><wide|A|\<vect\>>\<less\>0>n*q*<wide|v|\<vect\>>\<cdot\>\<mathd\><wide|A|\<vect\>>=+2*\<pi\><below|<above|<big|int>|cos
    \<eta\><rsub|max>>|0>n*q*<around*|(|\<mu\>*E-<around*|\||u|\|>*cos
    \<eta\>|)>R<rsup|2>*\<mathd\> <around*|(|cos \<eta\>|)>
  </equation*>

  where

  <\equation*>
    cos \<eta\><rsub|max>=<frac|\<mu\>*E|<around*|\||u|\|>><space|1em>limited
    to -1\<less\>cos \<eta\><rsub|max>\<less\>1
  </equation*>

  With <math|<around*|\||u|\|>=0> the entire sphere has the possibility of
  collecting particles; <math|\<eta\><rsub|max>=\<pi\>>.

  With <math|\<mu\>*E=0> (a grounded sphere), or in the high-wind limit
  <math|<around*|\||u|\|>\<gg\>\<mu\>*E>, only half the sphere (the windward
  side) is collecting; <math|\<eta\><rsub|max>=<frac|\<pi\>|2>>.

  Assuming positive carriers, an attracting sphere has <math|E\<less\>0>;
  <math|\<eta\><rsub|max>\<gtr\>\<pi\>/2> and some of the back side is
  shielded. The wind will eventually force a sign flip to <math|E\<gtr\>0>,
  so that <math|\<eta\><rsub|max>\<less\>\<pi\>/2> and some of the front side
  is shielded.

  The mobility current is

  <\equation*>
    I<rsub|mob>=2*\<pi\>*n*q*\<mu\>*E*R<rsup|2><below|<above|<big|int>|\<eta\><rsub|max>>|0>*\<mathd\>
    <around*|(|cos \<eta\>|)>=2*\<pi\>*n*q*\<mu\>*E*R<rsup|2>*<around*|(|cos
    \<eta\><rsub|max>-1|)>
  </equation*>

  and the wind current is

  <\equation*>
    I<rsub|wind>=-2*\<pi\>*n*q*<around*|\||u|\|>*R<rsup|2>*<below|<above|<big|int>|\<eta\><rsub|max>>|0><around*|(|cos
    \<eta\>|)>*\<mathd\> <around*|(|cos \<eta\>|)>=-2*\<pi\>*n*q*<around*|\||u|\|>*R<rsup|2><around*|(|<frac|cos<rsup|2>
    \<eta\><rsub|max>|2>-<frac|1|2>|)>
  </equation*>

  so the total current is

  <\equation*>
    I=I<rsub|mob>+I<rsub|wind>=2*\<pi\>*n*q*R<rsup|2>*<around*|[|\<mu\>*E*<around*|(|cos
    \<eta\><rsub|max>-1|)>-<frac|<around*|\||u|\|>|2>*<around*|(|cos<rsup|2>
    \<eta\><rsub|max>-1|)>|]>
  </equation*>

  <\equation*>
    <frac|\<mathd\>Q|\<mathd\>t>=2*\<pi\>*n*q*R<rsup|2>*<around*|[|\<mu\>*E*<around*|(|cos
    \<eta\><rsub|max>-1|)>-<frac|<around*|\||u|\|>|2>*<around*|(|cos<rsup|2>
    \<eta\><rsub|max>-1|)>|]>
  </equation*>

  <paragraph|Solving>

  By the relation between sphere charge and surface electric field,

  <\equation*>
    <frac|\<mathd\>Q|\<mathd\>t>=4*\<pi\>*\<varepsilon\><rsub|0>*R<rsup|2>*<frac|\<mathd\>E|\<mathd\>t>
  </equation*>

  <\equation*>
    <frac|\<mathd\>E|\<mathd\>t>=<frac|n*q|2*\<varepsilon\><rsub|0>>*<around*|[|\<mu\>*E*<around*|(|cos
    \<eta\><rsub|max>-1|)>-<frac|<around*|\||u|\|>|2>*<around*|(|cos<rsup|2>
    \<eta\><rsub|max>-1|)>|]>
  </equation*>

  <\with|color|pastel grey>
    Without the clamps on <math|cos \<eta\><rsub|max>>,

    <\equation*>
      <frac|\<mathd\>E|\<mathd\>t>=<frac|n*q|2*\<varepsilon\><rsub|0>>*<around*|[|\<mu\>*E*<around*|(|<frac|\<mu\>*E|<around*|\||u|\|>>-1|)>-<frac|<around*|\||u|\|>|2>*<around*|(|<frac|\<mu\>*E|<around*|\||u|\|>>-1|)>|]>
    </equation*>

    <\equation*>
      <frac|\<mathd\>E|\<mathd\>t>=A*E<rsup|2>+B*E+C
    </equation*>

    <\equation*>
      A=C*<frac|\<mu\><rsup|2>|<around*|\||u|\|>>
    </equation*>

    <\equation*>
      B=<frac|n*q|2*\<varepsilon\><rsub|0>>*<around*|(|-\<mu\>-<frac|\<mu\>|2>|)>=-<frac|3|2>*C*\<mu\>
    </equation*>

    <\equation*>
      C=<frac|n*q|2*\<varepsilon\><rsub|0>>
    </equation*>
  </with>

  \;

  <paragraph|Regimes>

  <\equation*>
    Q<rsub|0>=C*\<Phi\><rsub|0>=4*\<pi\>*\<varepsilon\><rsub|0>*R*\<Phi\><rsub|0>
  </equation*>

  <\equation*>
    \<tau\><rsub|\<sigma\>>=<frac|\<varepsilon\><rsub|0>|\<sigma\>>
  </equation*>

  <\equation*>
    I<rsub|wind>=n*Z*q<rsub|e>*v<rsub|wind> \<pi\>*R<rsup|2>
  </equation*>

  The only dimensionless ratio is

  <\equation*>
    \<frak-I\>\<equiv\><frac|I<rsub|wind>*\<tau\><rsub|\<sigma\>>|Q<rsub|0>>
  </equation*>

  A traditional relaxation probe has <math|\<frak-I\>=0> and pure wind has
  <math|\<frak-I\>\<rightarrow\>\<infty\>>.

  The ratio of charges is

  <\equation*>
    \<frak-I\>=<frac|n*Z*q<rsub|e>*v<rsub|wind> \<pi\>*R<rsup|2>
    <frac|\<varepsilon\><rsub|0>|\<sigma\>>|4*\<pi\>*\<varepsilon\><rsub|0>*R*\<Phi\><rsub|0>>=<frac|<frac|\<sigma\>|\<mu\>>*v<rsub|wind>*R|\<sigma\>*4*\<Phi\><rsub|0>>\<propto\><frac|v<rsub|wind>*R|\<mu\>
    \<Phi\><rsub|0>>=<frac|v<rsub|wind>|\<mu\>*E<rsub|0>>
  </equation*>

  and checking units,

  <\equation*>
    <around*|[|<frac|v<rsub|wind>*R|\<mu\>
    \<Phi\><rsub|0>>|]>=<around*|[|<frac|v<rsub|wind>|\<mu\>*E<rsub|0>>|]>=1
  </equation*>

  \;

  <paragraph|Conductivity from slope (no area correction)>

  <\equation*>
    Q=Q<rsub|0> \<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  \;

  At early-enough times

  \;

  <\equation*>
    Q=Q<rsub|0> <around*|(|1-<frac|t|\<tau\><rsub|\<sigma\>>>|)>+I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-<around*|(|1-<frac|t|\<tau\><rsub|\<sigma\>>>|)>|)>
  </equation*>

  <\equation*>
    Q=Q<rsub|0> <around*|(|1-<frac|t|\<tau\><rsub|\<sigma\>>>|)>+I<rsub|wind>*t
  </equation*>

  then

  <\equation*>
    slope=<frac|\<mathd\>|\<mathd\>t> log
    <around*|(|Q|)>=<frac|\<mathd\>|\<mathd\>t> log <around*|(|Q<rsub|0>
    <around*|(|1-<frac|t|\<tau\><rsub|\<sigma\>>>|)>+I<rsub|wind>*t|)>=<frac|\<mathd\>|\<mathd\>t>
    log <around*|(|Q<rsub|0> +<around*|(|I<rsub|wind>-<frac|Q<rsub|0>|\<tau\><rsub|\<sigma\>>>|)>*t|)>
  </equation*>

  <\equation*>
    slope=<frac|<around*|(|I<rsub|wind>-<frac|Q<rsub|0>|\<tau\><rsub|\<sigma\>>>|)>|Q<rsub|0>+<around*|(|I<rsub|wind>-<frac|Q<rsub|0>|\<tau\><rsub|\<sigma\>>>|)>*t>
  </equation*>

  and at <math|t=0> this becomes

  <\equation*>
    slope=<frac|I<rsub|wind>|Q<rsub|0>>-<frac|\<sigma\>|\<varepsilon\><rsub|0>>
  </equation*>

  \;

  \;

  At late-enough times

  <\equation*>
    slope=<frac|\<mathd\>|\<mathd\>t> log
    <around*|(|I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|)>|)>=<frac|1|I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|)>>*<frac|\<mathd\>|\<mathd\>t>
    I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  <\equation*>
    =<frac|1|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>>*<around*|(|+<frac|\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|\<tau\><rsub|\<sigma\>>>|)>=<frac|\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>|1-\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>>*<frac|\<sigma\>|\<varepsilon\><rsub|0>>\<approx\><frac|\<sigma\>|\<varepsilon\><rsub|0>>*\<bbb-e\><rsup|-t/\<tau\><rsub|\<sigma\>>>
  </equation*>

  <paragraph|Saturation potential>

  The saturation potential occurs when <math|<frac|\<mathd\>\<Phi\>|\<mathd\>t>=0>,
  giving

  <\equation*>
    0=Q<rsub|0> \<bbb-e\><rsup|-t<rsub|sat>/\<tau\><rsub|\<sigma\>>>+I<rsub|wind>*\<tau\><rsub|\<sigma\>>*<around*|(|1-\<bbb-e\><rsup|-t<rsub|sat>/\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  <\equation*>
    t<rsub|sat>=\<tau\><rsub|\<sigma\>>*log<around*|(|1-<frac|Q<rsub|0>|I<rsub|wind>*\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  There is no saturation if

  <\equation*>
    <frac|Q<rsub|0>|I<rsub|wind>*\<tau\><rsub|\<sigma\>>>\<geqslant\>1
  </equation*>

  \;

  Otherwise <math|\<tau\><rsub|\<sigma\>>> can be found by use of the product
  log function (Wolfram Alpha),

  <\equation*>
    \<tau\><rsub|\<sigma\>>=<frac|1|<frac|Q<rsub|0>|I<rsub|wind>>-t<rsub|sat>*W<around*|(|<frac|I<rsub|wind
    >t<rsub|sat>|Q<rsub|0>> exp<around*|(|<frac|I<rsub|wind
    >t<rsub|sat>|Q<rsub|0>>|)>|)>>
  </equation*>

  \;

  <paragraph|Oscillating field>

  Assuming <math|\<mathd\>t\<gg\>\<tau\><rsub|c>>, the constant-wind charging
  equation for a sphere is

  <\equation*>
    <wide|Q|\<dot\>>=-<frac|\<sigma\>|\<varepsilon\><rsub|0>>*Q+n*q
    v<rsub|wind>*A<rsub|eff>
  </equation*>

  Replacing

  <\equation*>
    v<rsub|wind>\<rightarrow\>\<mu\>*E<rsub|ext>*cos<around*|(|\<omega\>*t|)>
  </equation*>

  gives

  <\equation*>
    <wide|Q|\<dot\>>=\<sigma\>*E<rsub|ext>*cos<around*|(|\<omega\>*t|)>*A<rsub|eff>-<frac|\<sigma\>|\<varepsilon\><rsub|0>>*Q
  </equation*>

  <\equation*>
    <wide|Q|\<dot\>>=<frac|\<sigma\>|\<varepsilon\><rsub|0>><around*|(|\<varepsilon\><rsub|0>*E<rsub|ext>*cos<around*|(|\<omega\>*t|)>*A<rsub|eff>-Q|)>*
  </equation*>

  <\equation*>
    <wide|Q|\<dot\>>=<frac|Q<rsub|ext>*cos<around*|(|\<omega\>*t|)>-Q|\<tau\><rsub|\<sigma\>>>
  </equation*>

  where

  <\equation*>
    Q<rsub|ext>\<equiv\>\<varepsilon\><rsub|0>*E<rsub|ext>*A<rsub|eff>
  </equation*>

  Assuming for now that <math|A<rsub|eff>> is independent of time (though we
  know it is generally not, cf. uniform wind derivation), the solution is
  [Wolfram Alpha]

  <\equation*>
    Q<around*|(|t|)>=<frac|Q<rsub|ext>*<around*|(|\<omega\>*\<tau\><rsub|\<sigma\>>*sin<around*|(|\<omega\>*t|)>+cos<around*|(|\<omega\>*t|)>|)>|1+\<omega\><rsup|2>*\<tau\><rsub|\<sigma\>><rsup|2>>+C*exp<around*|(|-<frac|t|\<tau\><rsub|\<sigma\>>>|)>
  </equation*>

  where <math|C> is an integration constant.
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
    <associate|auto-6|<tuple|6|?>>
    <associate|auto-7|<tuple|7|?>>
    <associate|auto-8|<tuple|8|?>>
  </collection>
</references>

<\auxiliary>
  <\collection>
    <\associate|toc>
      <with|par-left|<quote|4tab>|Isolated sphere
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-1><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|With constant windspeed
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-2><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Wake/collection-area correction
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-3><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Solving
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-4><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Regimes
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-5><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Conductivity from slope (no area
      correction) <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-6><vspace|0.15fn>>

      <with|par-left|<quote|4tab>|Saturation potential
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-7><vspace|0.15fn>>
    </associate>
  </collection>
</auxiliary>