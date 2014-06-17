#Simulator Logic Overview

##Primary Goal
The goal of this project is to simulate potential economic conditions to provide a probability of success of a plan that
involves retiring early (before one has access to traditional retirement plans at the age of 59 1/2).

The 4 major inputs ares:

#### Initial Capital
#### Estimated Monthly Expenses
#### Years Until Early Retirement
#### Years Until Retirement Age ( 59 1/2)

A fundamental assumption here is that funds accumulated in retirement accounts will be sufficient to last after 59 1/2.
So the question being asked is how much money is needed to bridge a gap between an early retirement and penalty free access to funds.
This allows the capital to be much smaller since it will be possible to draw down on the the capital and not just utilize interest and dividends.

A second fundamental assumption is that historical market and inflation trends will in some way be reflective of the future.


## Secondary Goal
Once the primary goal is accomplished, a secondary goal is to provide a more traditional retirement calculator that has greater flexibility in 
the inputs.  For example, most retirement calculators make assumptions about spending in retirement based on current income.  This may be a 
reasonable assumption for the common use case, but can be misleading if one is saving a large percentage of money and intends to continue
living below their means in retirement. 

###Algorithmic Details
Simulating inflation or market returns should analyze historical data, determine the mean and standard deviation of that data and generate
a plausible simulated figure with the same mean and standard deviation.

Simulating a year of early retirement should simulate a market return and multiply this number time existing capital to determine how much 
money is generated that year.  Then estimated monthly expenses (times 12) are subtracted.  An adjustment must then be made for inflation (there
may be an error in that calculation currently).

Simulating early retirement is then done by repeating the process of simulating a year and keep track of the new figure of remaining capital.
Eventually, it may be desirable to have access to additional information such as the list of simulated market returns or how capital changed each year
instead of just a final result. 

The final unimplemented method aggregateSimulatedRetirements is supposed to simulate many early retirements and provide a probability of success,
where success is defined as not running out of money.  Additionally, it should provide a max, min, mean and potentially other descriptive statistics
of remaining capital at the end of the simulations. 



