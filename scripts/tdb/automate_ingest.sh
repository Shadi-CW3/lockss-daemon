#! /bin/bash
t="/home/$LOGNAME/tmp"
year=`date +%Y`

#Ready

#Highwire
echo "###Ready highwire" > $t/tmp_HW  #clear out file.
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "highwire" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | grep -v oxfordjournals | shuf >> $t/tmp_HW

#Atypon (Sage), in order by year
echo "###Ready Sage $year" > $t/tmp_Sage  #clear out file
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssSageAtyponJournals" and year is "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Sage
echo "###Ready Sage not $year" >> $t/tmp_Sage
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssSageAtyponJournals" and year is not "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Sage

#Atypon (not Sage), in order by year
echo "###Ready Atypon $year" > $t/tmp_Atypon  #clear out file
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "typon" and plugin !~ "SageAtyponJournals" and year is "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Atypon
echo "###Ready Atypon not $year" >> $t/tmp_Atypon
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "typon" and plugin !~ "SageAtyponJournals" and year is not "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Atypon

#Emerald Journals in order by year
echo "###Ready Emerald Journals $year" > $t/tmp_EmeraldJ  #clear out file
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssEmerald2020Plugin" and year is "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_EmeraldJ
echo "###Ready Emerald Journals not $year" >> $t/tmp_EmeraldJ
./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssEmerald2020Plugin" and year is not "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_EmeraldJ

#Manifest
#Don't include this year or last year so that new titles get tested for at least two years.
#echo "###Manifest Sage 2020" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2020$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#echo "###Manifest Sage 2010-2019" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2019$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2018$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2017$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2016$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2015$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2014$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2013$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2012$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2011$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2010$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#echo "###Manifest Sage 2009" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2009$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#echo "###Manifest Sage 2008" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2008$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#echo "###Manifest Sage 2007" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2007$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf >> $t/tmp_Atypon
#echo "###Manifest Sage 2006" >> $t/tmp_Atypon
#./scripts/tdb/tdbout -MT -a -Q 'year ~ "2006$" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/sage_publications.atypon.tdb | shuf | head >> $t/tmp_Atypon

#Atypon T&F in order by year
#Ready
#echo "###Ready T&F 2017" > $t/tmp_TnF
#./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssTaylorAndFrancisPlugin" and year is "2017" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_TnF
#echo "###Ready T&F not 2017" >> $t/tmp_TnF
#./scripts/tdb/tdbout -Y -a -Q 'plugin ~ "ClockssTaylorAndFrancisPlugin" and year is not "2017" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | shuf >> $t/tmp_TnF

#Misc: all but Highwire, T&F, Atypon (incl Sage), Emerald
#Ready
echo "###Ready Misc $year" > $t/tmp_Misc  #clear out file
./scripts/tdb/tdbout -Y -a -Q 'year is "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | grep -v highwire | grep -v typon | grep -v ClockssTaylorAndFrancisPlugin | grep -v ClockssEmerald2020Plugin | shuf >> $t/tmp_Misc
echo "###Ready Misc not $year" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'year is not "'$year'" and (au:hidden[proxy] is not set or au:hidden[proxy] is "")' tdb/clockssingest/*.tdb | grep -v highwire | grep -v typon | grep -v ClockssTaylorAndFrancisPlugin | grep -v ClockssEmerald2020Plugin | shuf >> $t/tmp_Misc
#Single Ingest Machines: all publishers
echo "*********************" >> $t/tmp_Misc
echo "###Ready Misc Ingest1" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'au:hidden[proxy] is "reingest1.clockss.org:8082"' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Misc
echo "*********************" >> $t/tmp_Misc
echo "###Ready Misc Ingest2" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'au:hidden[proxy] is "reingest2.clockss.org:8085"' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Misc
echo "*********************" >> $t/tmp_Misc
echo "###Ready Misc Ingest3" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'au:hidden[proxy] is "reingest3.clockss.org:8083"' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Misc
echo "*********************" >> $t/tmp_Misc
echo "###Ready Misc Ingest4" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'au:hidden[proxy] is "reingest4.clockss.org:8082"' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Misc
echo "*********************" >> $t/tmp_Misc
echo "###Ready Misc Ingest5" >> $t/tmp_Misc
./scripts/tdb/tdbout -Y -a -Q 'au:hidden[proxy] is "reingest5.clockss.org:8082"' tdb/clockssingest/*.tdb | shuf >> $t/tmp_Misc

head -n20 $t/tmp_HW | grep -v ClockssHWDrupalPlugin > $t/tmp_All
head -n20 $t/tmp_Sage >> $t/tmp_All
head -n40 $t/tmp_Atypon >> $t/tmp_All
head -n20 $t/tmp_EmeraldJ >> $t/tmp_All
head -n200 $t/tmp_Misc | grep -v HWDrupalPlugin >> $t/tmp_All

exit 0

# ./scripts/tdb/tdbedit --from-status=manifest,ready --to-status=crawling --auids=../SageEdits/tmp tdb/clockssingest/*.tdb
