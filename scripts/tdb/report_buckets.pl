#!/usr/bin/perl

#This script is designed to evaluate a set of AUs and deliver a chart showing
#status change from one point in time to another.

#GLN: To create report, comparing two points in time.
#git checkout master
#git checkout `git rev-list -n 1 --before="2020-04-01 00:00" master`
#ant jar-lockss
#/scripts/tdb/tdbout -t auid,status tdb/prod/{,*/}*.tdb | sort -u > ../tmp/file1.txt
#git checkout master
#git checkout `git rev-list -n 1 --before="2021-04-01 00:00" master`
#ant jar-lockss
#./scripts/tdb/tdbout -t auid,status tdb/prod/{,*/}*.tdb | sort -u > ../tmp/file2.txt
#./scripts/tdb/report_buckets.pl ../tmp/file1.txt ../tmp/file2.txt
#git checkout master
#git pull origin master
#ant jar-lockss

#CLOCKSS: To create a report, comparing clockss status1 and status2.
#git checkout master
#git checkout `git rev-list -n 1 --before="2023-05-01 00:00" master`
#ant jar-lockss
#./scripts/tdb/tdbout -t auid,status tdb/clockssingest/{,_retired/}*.tdb | sort -u > ../SageEdits/file_a.txt
#./scripts/tdb/tdbout -t auid,status2 tdb/clockssingest/{,_retired/}*.tdb | sort -u > ../SageEdits/file_b.txt
#./scripts/tdb/report_buckets.pl ../SageEdits/file_a.txt ../SageEdits/file_b.txt > ../SageEdits/buckets_today.tsv
#git checkout master
#git pull
#ant jar-lockss

#CLOCKSS: To create a report for FTP status2.
#git checkout master
#git checkout `git rev-list -n 1 --before="2022-05-01 00:00" master`
#ant jar-lockss
#./scripts/tdb/tdbout -t year,status2 tdb/clockssingest/{_source/,_retired/}*.source.tdb | sort | uniq -c > ../SageEdits/file_y.txt
#git checkout master
#git checkout `git rev-list -n 1 --before="2023-05-01 00:00" master`
#ant jar-lockss
#./scripts/tdb/tdbout -t year,status2 tdb/clockssingest/{_source/,_retired/}*.source.tdb | sort | uniq -c > ../SageEdits/file_z.txt
#git checkout master
#git pull
#ant jar-lockss


my %code = ("notPresent" => 0,
    "expected" => 1,
    "exists" => 2,
    "manifest" => 3,
    "wanted" => 4,
    "testing" => 5,
    "notReady" => 6,
    "ready" => 7,
    "crawling" => 8,
    "deepCrawl" => 9,
    "frozen" => 10,
    "ingNotReady" => 11,
    "finished" => 12,
    "released" => 13,
    "down" => 14,
    "superseded" => 15,
    "zapped" => 16,
    "doNotProcess" => 17,
    "doesNotExist" => 18,
    "other" => 19,
    "deleted" => 20);

my %auid_status = ();

my $file1_name = shift(@ARGV);
my $file2_name = shift(@ARGV);

# Read in first file. (Should be the report from the earlier
# date.
open(IFILE, "<$file1_name");
while (my $line = <IFILE>) {
    my ($auid, $status) = split(/\s+/, $line);
    my $status_code = $code{"other"};
    if (exists($code{$status})) {
        $status_code = $code{$status};
    }
    $auid_status{$auid}{start} = $status_code;
}
close(IFILE);

# Debug report
#foreach my $auid (keys(%auid_status)) {
#    printf("%s %d\n", $auid, $auid_status{$auid}{start});
#}

# Read in second file. (Should be the report from the later
# date.
open(IFILE, "<$file2_name");
while (my $line = <IFILE>) {
    my ($auid, $status) = split(/\s+/, $line);
    my $status_code = $code{"other"};
    if (exists($code{$status})) {
        $status_code = $code{$status};
    }
    $auid_status{$auid}{end} = $status_code;
}
close(IFILE);

# Clean up data structure by adding "notPresent" codes where
# things are missing.
foreach my $auid (keys(%auid_status)) {
    if (! exists($auid_status{$auid}{start})) {
        $auid_status{$auid}{start} = $code{"notPresent"};
    }
    if (! exists($auid_status{$auid}{end})) {
        $auid_status{$auid}{end} = $code{"deleted"};
    }
}

# Fill 2D array of buckets for each combination
# of start and end status
my @start_end = ();
# Initialize all to 0
foreach my $x (values(%code)) {
	foreach my $y (values(%code)) {
		$start_end[$x][$y] = 0;
	}
}
foreach my $auid (keys(%auid_status)) {
    $start_code = $auid_status{$auid}{start};
    $end_code = $auid_status{$auid}{end};
		$start_end[$start_code][$end_code] += 1;
		if ($end_code == $code{"other"}) {
			printf("Unexpected status:%s\n",$auid);
		}
#		if ($start_code == $code{"released"} && $end_code == $code{"deleted"}) {
#			printf("Previously released, now deleted:%s\n",$auid);
#		}
#		if ($start_code == $code{"down"} && $end_code == $code{"deleted"}) {
#			printf("Previously down, now deleted:%s\n",$auid);
#		}
#		if ($start_code == $code{"superseded"} && $end_code == $code{"deleted"}) {
#			printf("Previously superseded, now deleted:%s\n",$auid);
#		}
#		if ($start_code == $code{"released"} && $end_code == $code{"exists"}) {
#			printf("Previously released, now exists:%s\n",$auid);
#		}
#		if ($start_code == $code{"released"} && $end_code == $code{"manifest"}) {
#			printf("Previously released, now manifest:%s\n",$auid);
#		}
		if (($start_code == $code{"released"} || $start_code == $code{"down"} || $start_code == $code{"superseded"}) &&
		     !($end_code == $code{"released"} || $end_code == $code{"down"} || $end_code == $code{"superseded"})) {
			printf("Previously %s, now %s: %s\n",&code_name($start_code),&code_name($end_code),$auid);
		}
	}

# Print out report
# Header
printf("Status");
foreach my $y (sort by_value values(%code)) {
    printf("\t%s", &code_name($y));
}
print("\n");
foreach my $x (sort by_value values(%code)) {
    printf("%s", &code_name($x));
    foreach my $y (sort by_value values(%code)) {
        printf("\t%d", $start_end[$x][$y]);
    }
    print("\n");
}
print("\n");

exit(0);


sub code_name {
    my ($code_num) = @_;
    my $rval = "unknown";
    foreach my $cn (keys(%code)) {
        if ($code_num == $code{$cn}) {
            $rval = $cn;
            last;
        }
    }
  return($rval);
}

sub by_value {
    return($a <=> $b);
}
