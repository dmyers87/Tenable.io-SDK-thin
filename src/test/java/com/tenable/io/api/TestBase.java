package com.tenable.io.api;

import java.util.HashSet;
import java.util.Set;

import com.tenable.io.api.scans.models.Scan;
import com.tenable.io.api.scans.models.ScanDetails;
import com.tenable.io.api.scans.models.ScanListResult;
import com.tenable.io.api.scans.models.ScanStatus;
import com.tenable.io.core.exceptions.TenableIoException;


/**
 * Copyright (c) 2017 Tenable Network Security, Inc.
 */
public class TestBase {
    protected static final String TEST_SCAN_NAME_PREFIX = "tioTestScan_";
    protected static final String TEST_FOLDER_NAME_PREFIX = "tioTestFolder_";
    protected static final String TEST_AGENT_GROUP_NAME_PREFIX = "tioTestAgentGroup_";
    protected static final String TEST_TARGET_GROUP_PREFIX = "tioTestTargetGroup_";
    protected static final String TEST_EXCLUSION_NAME_PREFIX = "tioTestExclusion_";
    protected static final String TEST_GROUP_NAME_PREFIX = "tioTestGroup_";
    protected static final String TEST_POLICY_NAME_PREFIX = "tioTestPolicy_";
    protected static final String TEST_SCANNER_GROUP_NAME_PREFIX = "tioTestScannerGroup_";
    protected static final String TEST_TAG_NAME_PREFIX = "tioTestTag_";
    protected static final String TEST_ACCESS_GROUP_NAME_PREFIX = "tioTestAccessGroup_";


    protected TenableIoClient apiClient = new TenableIoClient();
    private static final String testUsernameBase = "tioTestUsername-automation";
    private Set<String> testUsernames = new HashSet<>();

    // A valid domain name for username
    private String testDomain = System.getProperty( "userDomainName" );
    // Host target to create a scan with. (Warning: do not scan targets that you're not authorized to.)
    private String scanTextTargets = System.getProperty( "scanTextTargets" );
    // Host alternative target to launch a scan with. (Warning: do not scan targets that you're not authorized to.)
    private String scanAltTargets = System.getProperty( "scanAltTargets" );
    // Name of template to create a scan with.
    private String scanTemplateName = System.getProperty( "scanTemplateName" );
    // Name of template to create a policy with.
    private String policyTemplateName = System.getProperty( "policyTemplateName" );

    public String getTestDomain() {
        return testDomain;
    }


    public String getScanTextTargets() {
        return scanTextTargets;
    }


    public String getScanAltTargets() {
        return scanAltTargets;
    }


    public String getScanTemplateName() {
        return scanTemplateName;
    }


    public String getPolicyTemplateName() {
        return policyTemplateName;
    }


    protected String getTestUsername( int number ) {
        if( getTestDomain() == null )
            throw new IllegalArgumentException( "JVM property \"userDomainName\" needs to be set prior to running the tests" );

        String username = String.format( "%s%d@%s", testUsernameBase, number, getTestDomain() );
        testUsernames.add( username );
        return username;
    }

    private void closeClient() {
        try {
            apiClient.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void deleteTestScans( TenableIoClient apiClient ) throws TenableIoException {
        // delete potential test policies
        ScanListResult scanListResult = apiClient.getScansApi().list();
        if( scanListResult != null ) {
            for( Scan scan : scanListResult.getScans() ) {
                if( scan.getName().toLowerCase().startsWith( TEST_SCAN_NAME_PREFIX.toLowerCase() ) ) {
                    try {
                        apiClient.getScanHelper().getScan( scan.getId() ).stop();
                    } catch( TenableIoException te ) {
                        // could be a state conflict if the scan was already stopped
                    }
                    apiClient.getScansApi().delete( scan.getId() );
                }
            }
        }
    }


    protected String getNewTestScanName() {
        return getNewTestName( TEST_SCAN_NAME_PREFIX );
    }

    protected String getNewTestFolderName() {
        return getNewTestName( TEST_FOLDER_NAME_PREFIX );
    }

    protected String getNewTestAgentGroupName() {
        return getNewTestName( TEST_AGENT_GROUP_NAME_PREFIX );
    }

    protected String getNewTestTargetGroupName() {
        return getNewTestName( TEST_TARGET_GROUP_PREFIX );
    }


    protected String getNewTestExclusionName() {
        return getNewTestName( TEST_EXCLUSION_NAME_PREFIX );
    }


    protected String getNewTestGroupName() {
        return getNewTestName( TEST_GROUP_NAME_PREFIX );
    }

    protected String getNewTestPolicyName() {
        return getNewTestName( TEST_POLICY_NAME_PREFIX );
    }

    protected String getNewTestScannerGroupName() {
        return getNewTestName( TEST_SCANNER_GROUP_NAME_PREFIX );
    }

    protected String getNewTestTagName() {
        return getNewTestName( TEST_TAG_NAME_PREFIX );
    }

    protected String getNewTestAccessGroupName() {
        return getNewTestName( TEST_ACCESS_GROUP_NAME_PREFIX );
    }


    private String getNewTestName( String prefix ) {
        return prefix + java.util.UUID.randomUUID().toString().substring( 0, 6 );
    }


    protected ScanStatus waitForStatus( TenableIoClient apiClient, int scanId, ScanStatus status ) throws TenableIoException, InterruptedException {
        ScanDetails details = apiClient.getScansApi().details( scanId );
        ScanStatus curStatus = details.getInfo().getStatus();
        while ( curStatus != status ) {
            Thread.sleep( 5000 );
            if ( details.getInfo().getScheduleUuid() == null || details.getInfo().getUuid() == null ) {
                details = apiClient.getScansApi().details( scanId );
                curStatus = details.getInfo().getStatus();
            }
            else curStatus = apiClient.getScansApi().getScanHistoryStatus( details.getInfo().getScheduleUuid(), details.getInfo().getUuid() ).getStatus();
        }

        return curStatus;
    }
}

