Lai piemērotu aprakstītu k-tuvāko kaimiņu klasificēšanas un regresijas metodi dotajam uzdevumam – ierīces iekštelpu pozicionēšanai, balstoties uz bezvada tīkla piekļuves mezglu signāla stiprumiem – tika uzrakstīta komandrindas lietotne. Šai programmai ir trīs izpildes režīmi: 
*	prognozēšana; 
*	validēšana; 
*	analīze. 
Prognozēšanas režīmā lietotājs pieprasa prognozēt ierīces iekštelpa koordinātes pēc dotajiem tīkla piekļuves punktu signālu stiprumiem. Pieprasīti ievades dati: 
*	ceļš pie datnes, kas satur apmācības datu kopu (noformētu līdzīgi UJIIndoorLoc apmācības datu kopai); 
*	k-tuvāko kaimiņu metodes konfigurācija (kaimiņu skaits, attāluma metrika, svara funkcija); 
*	signāla stiprumi (pēc noklusējuma visiem piekļuves punktiem signāla stiprums ir stāvoklī “nav uztverts”). 

Validēšanas (testēšanas) režīmā lietotājs var pieprasīt dotas metodes konfigurācijas novērtējumu, lai uzzinātu tas efektivitāti un vēlāk salīdzināt ar citām konfigurācijām. Pieprasīti ievades dati: 
*	ceļš pie datnes, kas satur apmācības datu kopu (noformētu līdzīgi UJIIndoorLoc apmācības datu kopai); 
*	ceļš pie datnes, kas satur validēšanas datu kopu (noformētu līdzīgi UJIIndoorLoc validēšanas datu kopai); 
*	k-tuvāko kaimiņu metodes konfigurācija (kaimiņu skaits, attāluma metrika, svara funkcija); 

Konfigurācijas novērtēšanai ir lietoti divi mēri: 
*	attiecība starp pareizi prognozēto stāvu skaitu pret kopējo validēšanas kopas apjomu (ņem vērā svarīgāko dimensiju – augstumu); 
*	kvadrātsakne no vidējās kvadrātiskās kļūdas (ņem vērā visas trīs dimensijas), metros.

Analīzes režīmā lietotājs var pieprasīt vairāku metodes konfigurāciju novērtējumu aprēķināšanu. Konfigurāciju kopa ir iepriekš uzdota ar programmas izstrādātāju (nepieciešamības gadījumā var ienest izmaiņas pirmkodā). Aprēķinātas konfigurācijas kļūdas tiek formatētā veidā ierakstītas datnē, datus no kuras var eksportēt elektronisko izklājlapu rīkā tālākai analīzei. 
Jo analīzes process var aizņemt daudz laikā, lietotājs tiek informēts par modeļu validēšanas gaitu (kols – viena konfigurācija). Dati par modeļu validēšanu tiek saglabāti īslaicīgā datnē, tāpēc procesu var pārtraukt un turpināt vēlāk, palaižot analīzes procesu no jauna. 
Pieprasīti ievades dati: 
*	ceļš pie datnes, kas satur apmācības datu kopu (noformētu līdzīgi UJIIndoorLoc apmācības datu kopai); 
*	ceļš pie datnes, kas satur validēšanas datu kopu (noformētu līdzīgi UJIIndoorLoc validēšanas datu kopai). 
