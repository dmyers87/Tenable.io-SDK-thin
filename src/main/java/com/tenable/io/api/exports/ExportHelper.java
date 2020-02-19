package com.tenable.io.api.exports;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.tenable.io.api.ApiHelperBase;
import com.tenable.io.api.TenableIoClient;
import com.tenable.io.api.exports.models.AssetsExportRequest;
import com.tenable.io.api.exports.models.ExportStatus;
import com.tenable.io.api.exports.models.Status;
import com.tenable.io.api.exports.models.VulnsExportRequest;
import com.tenable.io.core.exceptions.TenableIoException;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (c) 2018 Tenable Network Security, Inc.
 */
@Slf4j
public class ExportHelper extends ApiHelperBase {
    private static final int sleepInterval = 5000;
    private TenableIoClient client;

    /**
     * Instantiates a new Export helper.
     *
     * @param client the client
     */
    public ExportHelper( TenableIoClient client ) {
        this.client = client;
        path = client.getExportsApi().getUriString("/export");
    }


    /**
     * Request the vulns export chunks, poll for status, and download them when it's available. The chunks will be
     * downloaded in no particular order.
     *
     * @param downloadPath File Path to save the chunk downloads to
     * @param exportRequest The VulnsExportRequest to set filters
     * @throws TenableIoException
     * @return
     */
    public List<File> downloadVulns( String downloadPath, VulnsExportRequest exportRequest  ) throws TenableIoException {
        String exportUuid = client.getExportsApi().vulnsRequestExport( exportRequest );
        ExportStatus status = this.client.getExportsApi().vulnsExportStatus( exportUuid );
        while( !status.getStatus().equals( Status.FINISHED ) ) {
            try {
                Thread.sleep( sleepInterval );
            } catch( InterruptedException e ) {
                log.error(e.getMessage());
            }

            status = this.client.getExportsApi().vulnsExportStatus( exportUuid );
        }

        // Download chunks
        return status.getChunksAvailable().stream().map(chunkId -> {
            File downloadFile = new File( downloadPath + "_" + chunkId);
            try {
                this.client.getExportsApi().vulnsDownloadChunk( exportUuid, chunkId, downloadFile );
            } catch (TenableIoException e) {
                // log the exception but keep processing
                log.error(e.getMessage());
            }
            log.info("Saved TenableIO chunk to file {}", downloadFile.getPath());
            return downloadFile;
        }).collect(Collectors.toList());

    }


    /**
     * Request the vulns export chunks, poll for status, and download them when it's available. The chunks will be
     * downloaded in no particular order.
     *
     * @param downloadPath File Path to save the chunk downloads to
     * @param exportRequest The VulnsExportRequest to set filters
     * @throws TenableIoException
     */
    public void downloadAssets( String downloadPath, AssetsExportRequest exportRequest ) throws TenableIoException {
        String exportUuid = client.getExportsApi().assetsRequestExport( exportRequest );
        ExportStatus status = this.client.getExportsApi().assetsExportStatus( exportUuid );

        while( !status.getStatus().equals( Status.FINISHED ) ) {
            try {
                Thread.sleep( sleepInterval );
            } catch( InterruptedException e ) {
            }

            status = this.client.getExportsApi().assetsExportStatus( exportUuid );
        }

        // Download chunks
        for ( int id : status.getChunksAvailable() ) {
            File downloadFile = new File( downloadPath + "_" + id );
            client.getExportsApi().assetsDownloadChunk( exportUuid, id, downloadFile );
        }

    }

}
