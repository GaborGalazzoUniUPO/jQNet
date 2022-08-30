# QNetSim - Simulatore di Reti di Code

### Gabor Galazzo | Valutazione Delle Prestazioni | UPO | 2022/23

![none](doc/img/img_1.png)

QNetSim è uno strumento di simulazione e valutazione di metriche per reti di code realizzato come parte di laboratorio
del corso di Valutazione delle Prestazioni presso l'università degli studi del Piemonte Orientale a.a. 2022/23

## Quick Start

Una volta avviato l'applicativo si aprerà la schermata seguente
![](doc/img/img_2.png)

#### Network Configuration

1. **Select Model**: Seleziona il file JSON contenente la rete su cui effetuare la simulazione
2. **Reference Station**: Stazione di riferimento per la terminazione

#### Simulation Configuration

3. **Customer for Run**: Numero di clienti serviti prima di terminare la simulazione dalla stazione di riferimento
4. **Num Runs**: Numero di run da effettuare (in parallelo)

#### Evaluation Coefficients

5. **Accuracy**: L'accuratezza in percentuale desiderata per le metriche
6. **Precision Type**: Scelta della precisione se Assoluta o Relativa
7. **Precision**: Precisione dell'accuratezza delle metriche

#### Interface

8. **Run Simulation**: Avvia la simulazione
9. **Network**: Rappresentazione grafica della Rete di Code

Una volta avviata la simulazione comparirà una dinestra con una barra progresso in cui vengono riportate le metriche
generate ad ogni run di simulazione

#### Risultati Run

![](doc/img/img_3.png)

1. **Barra Progresso**
2. **Node**: Il nodo della rete relativo alla metrica
3. **Metrics**: La metrica valutata dul nodo
4. **Mean**: Il valore medio della metrica sulle più run
5. **SD**: Deviazione Standard della metrica sulle più run
6. **Coefficient**: T COnfidence Interval
7. **Min Runs**: Stima delle run necessarie a raggiungere l'accuracy desiderata con la precision specificata
8. **Accuracy Reached**: Indica se l'accuracy desiderata con tale precisione è stata raggiunta

## Formato Json dei modelli

I json file contengono un JSONObject che descrive la rete.
Ogni Classe del progetto relativa al modello presenta un costruttore che prende in input
un oggetto json.

```json
{
  // Oggetto Principale QueueNetwork
  "class": "uniupo.valpre.bcnnsim.network.QueueNetwork",
  // Indica le classi di clienti disponibili
  "classes": [
    {
      "class": "uniupo.valpre.bcnnsim.network.classes.OpenCustomerClass",
      "name": "c",
      "priority": 0,
      "referenceStation": "source",
      "interArrivalTimeDistribution": {
        "class": "uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution",
        "mean": 1.0
      }
    }
  ],
  // Indica i Nodi della Rete
  "nodes": [
    {
      // Nodo di tipo "Coda"
      "class": "uniupo.valpre.bcnnsim.network.node.Queue",
      "name": "q1",
      "routingStrategy": {
        "class": "uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy"
      },
      "numServer": 2
    },
    {
      // Nodo di tipo "Pozzo"
      "class": "uniupo.valpre.bcnnsim.network.node.Sink",
      "name": "sink"
    },
    {
      // Nodo di tipo "Sorgente"
      "class": "uniupo.valpre.bcnnsim.network.node.Source",
      "name": "source",
      "routingStrategy": {
        "class": "uniupo.valpre.bcnnsim.network.routing.RandomRoutingStrategy"
      }
    }
  ],
  // Indica le distribuzioni per nodo e classe per il routing
  "serviceTimeDistributions": [
    {
      "node": "q1",
      "class": "c",
      "distribution": {
        "class": "uniupo.valpre.bcnnsim.random.distribution.PositiveNormalDistribution",
        "mean": 3.2,
        "sigma": 0.6
      }
    },
    {
      "node": "source",
      "class": "c",
      "distribution": {
        "class": "uniupo.valpre.bcnnsim.random.distribution.ExponentialDistribution",
        "mean": 4.2
      }
    }
  ],
  // Collegamenti tra i nodi
  "links": {
    "q1": [
      "sink"
    ],
    "sink": [],
    "source": [
      "q1"
    ]
  }
}
```

## Scelte implementative

### Package Diagram

```plantuml
@startuml
package uniupo.valpre.bcnnsim <<Frame>> {
    package sim <<Frame>> {
        class Simulator
        class MultiRunNetworkReport
        class NetworkReport
        class NodeReport
        
        Simulator --> MultiRunNetworkReport
        MultiRunNetworkReport --* NetworkReport
        NetworkReport --* NodeReport
    }
    package gui <<Frame>> {
        class MainGUI
        class ReportGUI
        class NetworkVisualizer
    }
    class Main {
    }
    
    Main --> MainGUI
    MainGUI --> ReportGUI
    MainGUI --> NetworkVisualizer
    MainGUI --> Simulator
   
    
    package random <<Frame>> {
        interface MultipleStreamGenerator
        interface RandomGenerator
        
        MultipleStreamGenerator --* RandomGenerator
    }
    
    package network <<Frame>> {
        package classes <<Frame>> {
            abstract CustomerClass
            class ClosedCustomerClass
            class OpenCustomerClass
            
            ClosedCustomerClass --|> CustomerClass
            OpenCustomerClass --|> CustomerClass
            
       
        }
        package event <<Frame>> {
        
            abstract Event
            class ArrivalEvent
            class DepartureEvent
            
            Event --|> ArrivalEvent
            Event --|> DepartureEvent
       
        }
       
        package routing <<Frame>> {
        
            abstract RoutingStrategy
            class ProbabilityRoutingStrategy
            class RandomRoutingStrategy
            
            ProbabilityRoutingStrategy --|> RoutingStrategy
            RandomRoutingStrategy --|> RoutingStrategy
            
       
        }
        
         package node <<Frame>> {
        
            abstract Node
            class Delay
            class Queue
            class Sink
            class Source
            
            Delay --|> Node
            Queue --|> Node
            Sink --|> Node
            Source --|> Node
            
            
            Node --> Event
            Node --> RoutingStrategy
            Node --> CustomerClass
            Node --> RandomGenerator
       
        }
        
      
        
        class QueueNetwork
        QueueNetwork --> CustomerClass
        QueueNetwork --> Node
        QueueNetwork --> RandomGenerator
        QueueNetwork --> MultipleStreamGenerator
                
        MainGUI --> QueueNetwork
        Simulator --> QueueNetwork
    }
   
}
@enduml
```
### Descrizioni
#### gui
Questo Package contiene i form grafici e le schemate di visualizzazione
#### network
Network è il package che contiene la rappresentazione in oggetti di una rete di code
##### network.class
Contiene i descrittori di Classi di Customer: Open e Closed
##### network.event
Contiene i descrittori di Event: Arrivo e Partenza
##### network.node
Conitene i descrittori dei nodi della rete:
- Source
- Queue
- Delay
- Sink
##### network.routing
Contiene i descrittori delle strategie di Routing
## Gestione dei Generatori di Sequenza Casuali
```plantuml
@startuml
package uniupo.valpre.bcnnsim.random <<Frame>> {
    interface RandomGenerator
    interface MultipleStreamGenerator
    
    class LehmerGenerator
    class MultipleLehmerStreamGenerator
    
    LehmerGenerator --|> RandomGenerator
    MultipleLehmerStreamGenerator --|> MultipleStreamGenerator
    
    MultipleStreamGenerator --> RandomGenerator
    MultipleLehmerStreamGenerator --> LehmerGenerator

    package distribution <<Frame>> {
       
        
        class BernoulliDistribution
        class BinomialDistribution
        class ChiSquareDistribution
        class EquilikelyDistribution
        class ErlangDistribution
        class ExponentialDistribution
        class GeometricDistribution
        class HyperExponentialDistribution
        class LogNormalDistribution
        class NormalDistribution
        class PositiveNormalDistribution
        class StudentDistribution
        class UniformDistribution
        
        BernoulliDistribution -down-|> Distribution
        BinomialDistribution -down-|> Distribution
        ChiSquareDistribution -down-|> Distribution
        EquilikelyDistribution -down-|> Distribution
        ErlangDistribution -left-|> Distribution
        ExponentialDistribution -left-|> Distribution
        GeometricDistribution -left-|> Distribution
        HyperExponentialDistribution -left-|> Distribution
        LogNormalDistribution -up-> Distribution
        NormalDistribution -up-|> Distribution
        PositiveNormalDistribution -up-|> Distribution
        StudentDistribution -up-|> Distribution
        UniformDistribution -up-|> Distribution
        
         abstract Distribution {
            method generate(RandomGenerator)
        }
        
        Distribution -> RandomGenerator
        
        
        
        
        
    }
}
@enduml
```
### Generazione di numeri casuali
Per generare delle sequenza lunghe di numeri casuali si è utilizzato il generatore di **Lehmer** implementato cella classe 
[LehmerGenerator](src/main/java/uniupo/valpre/bcnnsim/random/LehmerGenerator.java)
### Gestione Sequenze lunghe ed indipendenti
Si sono utilizzate le proprietà del generatore di Lehmer per generare 256 Stream indipendenti. 
Il codice è stato implementato nella classe [MultipleLehmerStreamGenerator](src/main/java/uniupo/valpre/bcnnsim/random/MultipleLehmerStreamGenerator.java)
### Implementazione dei campionatori da una distribuzione Nota
All'interno del package distribution sono presenti le implementazioni di generatori di numeri casuali per le principali distribuzioni Note.
Tutte implementano il metodo *generate(RandomGenerator g)* che prende in input un generatore g un uniforme [0,1] ed effettuano le trasformazioni  
Le distribuzioni implementate sono
* [Bernoulli](src/main/java/uniupo/valpre/bcnnsim/random/distribution/BernoulliDistribution.java)
* [Binomial](src/main/java/uniupo/valpre/bcnnsim/random/distribution/BinomialDistribution.java)
* [ChiSquare](src/main/java/uniupo/valpre/bcnnsim/random/distribution/ChiSquareDistribution.java)
* [Equilikely](src/main/java/uniupo/valpre/bcnnsim/random/distribution/EquilikelyDistribution.java)
* [Erlang](src/main/java/uniupo/valpre/bcnnsim/random/distribution/ErlangDistribution.java)
* [Exponential](src/main/java/uniupo/valpre/bcnnsim/random/distribution/ExponentialDistribution.java)
* [Geometric](src/main/java/uniupo/valpre/bcnnsim/random/distribution/GeometricDistribution.java)
* [HyperExponential](src/main/java/uniupo/valpre/bcnnsim/random/distribution/HyperExponentialDistribution.java)
* [LogNormal](src/main/java/uniupo/valpre/bcnnsim/random/distribution/LogNormalDistribution.java)
* [Normal](src/main/java/uniupo/valpre/bcnnsim/random/distribution/NormalDistribution.java)
* [PositiveNormal](src/main/java/uniupo/valpre/bcnnsim/random/distribution/PositiveNormalDistribution.java)
* [Student](src/main/java/uniupo/valpre/bcnnsim/random/distribution/StudentDistribution.java)
* [Uniform](src/main/java/uniupo/valpre/bcnnsim/random/distribution/UniformDistribution.java)

## Implementazione della Simulazione

```plantuml
@startuml
(*) --> "Load della rete da file e degli input" 
partition Simulator {
 --> "runSimulation()"

--> ===B1===
--> "Creo una FEL"
--> "Genero una Rete Pulita dal modello in input"
--> "Per ogni classe di clienti 
aggiongo alla FEL gli eventi di 
arrivo sulla stazione di riferimento"
--> "inizio simulazione"
If "simulazione terminata" then
--> [Yes] "Genero il REPORT per la simulazione"
else
--> "Estraggo un evento dalla FEL"
--> "Ottengo la stazione (Nodo) di riferimento di quell'evento"

partition Node {
 --> "manageEvent()"
 --> "Aggiorno gli accumulatori generici"
 If "L'evento è un arrivo" then
 --> [Yes] "Gestisco Arrivo"
 --> "Aggiorno gli accumulatori specifici"
 --> "Aggiungo il cliente alla coda"
     If "Ci sono dei servitori liberi" then
     --> [Yes] Assegno il cliente ad un servitore
     --> Genero l'evento di fine Servizio
     --> "Aggiorno il tempo 
     di Simulazione"
     else
     --> "Aggiorno il tempo 
     di Simulazione"
     EndIf
 else
 --> [Partenza] "Gestisco la partenza"
 --> "Libero un servitore"
 If "C'è un cliente in coda" then
 --> [Yes]  Assegno il primo cliente ad un servitore
 --> Genero l'evento di fine servizio
 --> Aggiorno l'accumulatore del tempo di coda
 --> "Aggiorno gli acccumulatori Specifici"
 else
 --> "Aggiorno gli acccumulatori Specifici"
 EndIf
 --> "Genero l'evento di arrivo 
 alla stazione sucessiva"
 Endif
 --> "Aggiorno il tempo 
 di Simulazione"
 --> "Restituisco gli eventi 
 da aggiungere nella FEL"

 
 
 
 
}
--> "Aggiungo alla FEL gli eventi generati"
--> "Se l'evento era una Partenza aggiorno 
l'indicatore di progresso e terminazione"


-down-> "inizio simulazione"


"Genero il REPORT per la simulazione" --> ===B2===

===B1=== --> "..."

--> ===B2===
}
--> (*)

@enduml
```
La procedura di simulazione è implementata nella classe [Simulator](https://github.com/GaborGalazzoUniUPO/jQNet/blob/98a4409a461de6922f50f46f01fd1184a295e2af/src/main/java/uniupo/valpre/bcnnsim/sim/Simulator.java#L35)
e poi nelle varie implementazioni del metodo **manageEvent(Event e)** dei [nodi](https://github.com/GaborGalazzoUniUPO/jQNet/blob/98a4409a461de6922f50f46f01fd1184a295e2af/src/main/java/uniupo/valpre/bcnnsim/network/node/Queue.java#L54) 

## Confronto con Java Modeling Tool
