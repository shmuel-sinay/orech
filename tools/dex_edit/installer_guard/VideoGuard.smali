.class public final Lcom/android/packageinstaller/VideoGuard;
.super Ljava/lang/Object;
.source "VideoGuard.java"


# static fields
.field private static final LIBS:[Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .registers 3

    .line 13
    const/16 v0, 0xb

    new-array v0, v0, [Ljava/lang/String;

    const/4 v1, 0x0

    const-string v2, "libvlc"

    aput-object v2, v0, v1

    const/4 v1, 0x1

    const-string v2, "libavcodec"

    aput-object v2, v0, v1

    const/4 v1, 0x2

    const-string v2, "libffmpeg"

    aput-object v2, v0, v1

    const/4 v1, 0x3

    const-string v2, "libmpv"

    aput-object v2, v0, v1

    const/4 v1, 0x4

    const-string v2, "libdav1d"

    aput-object v2, v0, v1

    const/4 v1, 0x5

    const-string v2, "libvpx"

    aput-object v2, v0, v1

    const/4 v1, 0x6

    const-string v2, "libde265"

    aput-object v2, v0, v1

    const/4 v1, 0x7

    const-string v2, "libopenh264"

    aput-object v2, v0, v1

    const/16 v1, 0x8

    const-string v2, "libx264"

    aput-object v2, v0, v1

    const/16 v1, 0x9

    const-string v2, "libijkffmpeg"

    aput-object v2, v0, v1

    const/16 v1, 0xa

    const-string v2, "libijksdl"

    aput-object v2, v0, v1

    sput-object v0, Lcom/android/packageinstaller/VideoGuard;->LIBS:[Ljava/lang/String;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static blocked(Ljava/lang/String;)Z
    .registers 7

    .line 21
    const/4 v0, 0x0

    if-nez p0, :cond_4

    return v0

    .line 22
    :cond_4
    nop

    .line 24
    const/4 v1, 0x0

    :try_start_6
    new-instance v2, Ljava/util/zip/ZipFile;

    invoke-direct {v2, p0}, Ljava/util/zip/ZipFile;-><init>(Ljava/lang/String;)V
    :try_end_b
    .catchall {:try_start_6 .. :try_end_b} :catchall_78

    .line 25
    :try_start_b
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->entries()Ljava/util/Enumeration;

    move-result-object p0

    .line 26
    :goto_f
    invoke-interface {p0}, Ljava/util/Enumeration;->hasMoreElements()Z

    move-result v1

    if-eqz v1, :cond_6e

    .line 27
    invoke-interface {p0}, Ljava/util/Enumeration;->nextElement()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/zip/ZipEntry;

    .line 28
    invoke-virtual {v1}, Ljava/util/zip/ZipEntry;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v3

    .line 29
    const-string v4, "lib/"

    invoke-virtual {v3, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    const/4 v5, 0x1

    if-eqz v4, :cond_58

    const-string v4, ".so"

    invoke-virtual {v3, v4}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_58

    .line 30
    const/16 v1, 0x2f

    invoke-virtual {v3, v1}, Ljava/lang/String;->lastIndexOf(I)I

    move-result v1

    add-int/2addr v1, v5

    invoke-virtual {v3, v1}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    .line 31
    move v3, v0

    :goto_40
    sget-object v4, Lcom/android/packageinstaller/VideoGuard;->LIBS:[Ljava/lang/String;

    array-length v4, v4

    if-ge v3, v4, :cond_6c

    .line 32
    sget-object v4, Lcom/android/packageinstaller/VideoGuard;->LIBS:[Ljava/lang/String;

    aget-object v4, v4, v3

    invoke-virtual {v1, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4
    :try_end_4d
    .catchall {:try_start_b .. :try_end_4d} :catchall_75

    if-eqz v4, :cond_55

    .line 41
    :try_start_4f
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_52
    .catchall {:try_start_4f .. :try_end_52} :catchall_53

    goto :goto_54

    :catchall_53
    move-exception p0

    .line 32
    :goto_54
    return v5

    .line 31
    :cond_55
    add-int/lit8 v3, v3, 0x1

    goto :goto_40

    .line 34
    :cond_58
    :try_start_58
    const-string v4, "androidmanifest.xml"

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_6c

    .line 35
    invoke-static {v2, v1}, Lcom/android/packageinstaller/VideoGuard;->manifestDeclaresVideo(Ljava/util/zip/ZipFile;Ljava/util/zip/ZipEntry;)Z

    move-result v1
    :try_end_64
    .catchall {:try_start_58 .. :try_end_64} :catchall_75

    if-eqz v1, :cond_6d

    .line 41
    :try_start_66
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_69
    .catchall {:try_start_66 .. :try_end_69} :catchall_6a

    goto :goto_6b

    :catchall_6a
    move-exception p0

    .line 35
    :goto_6b
    return v5

    .line 34
    :cond_6c
    nop

    .line 37
    :cond_6d
    goto :goto_f

    .line 41
    :cond_6e
    :try_start_6e
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_71
    .catchall {:try_start_6e .. :try_end_71} :catchall_72

    goto :goto_74

    :catchall_72
    move-exception p0

    .line 42
    nop

    .line 43
    :goto_74
    return v0

    .line 38
    :catchall_75
    move-exception p0

    move-object v1, v2

    goto :goto_79

    :catchall_78
    move-exception p0

    .line 39
    :goto_79
    nop

    .line 41
    if-eqz v1, :cond_81

    :try_start_7c
    invoke-virtual {v1}, Ljava/util/zip/ZipFile;->close()V
    :try_end_7f
    .catchall {:try_start_7c .. :try_end_7f} :catchall_80

    goto :goto_81

    :catchall_80
    move-exception p0

    .line 39
    :cond_81
    :goto_81
    return v0
.end method

.method private static indexOf([BI[B)I
    .registers 8

    .line 71
    const/4 v0, 0x0

    move v1, v0

    :goto_2
    array-length v2, p2

    add-int/2addr v2, v1

    if-gt v2, p1, :cond_19

    .line 72
    move v2, v0

    :goto_7
    array-length v3, p2

    if-ge v2, v3, :cond_18

    .line 73
    add-int v3, v1, v2

    aget-byte v3, p0, v3

    aget-byte v4, p2, v2

    if-eq v3, v4, :cond_15

    .line 71
    add-int/lit8 v1, v1, 0x1

    goto :goto_2

    .line 72
    :cond_15
    add-int/lit8 v2, v2, 0x1

    goto :goto_7

    .line 75
    :cond_18
    return v1

    .line 77
    :cond_19
    const/4 p0, -0x1

    return p0
.end method

.method private static manifestDeclaresVideo(Ljava/util/zip/ZipFile;Ljava/util/zip/ZipEntry;)Z
    .registers 6
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 47
    invoke-virtual {p0, p1}, Ljava/util/zip/ZipFile;->getInputStream(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;

    move-result-object p0

    .line 49
    :try_start_4
    invoke-virtual {p1}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v0

    const-wide/16 v2, 0x400

    invoke-static {v0, v1, v2, v3}, Ljava/lang/Math;->max(JJ)J

    move-result-wide v0

    long-to-int p1, v0

    new-array p1, p1, [B

    .line 50
    const/4 v0, 0x0

    move v1, v0

    .line 52
    :cond_13
    :goto_13
    array-length v2, p1

    sub-int/2addr v2, v1

    invoke-virtual {p0, p1, v1, v2}, Ljava/io/InputStream;->read([BII)I

    move-result v2

    if-lez v2, :cond_2a

    .line 53
    add-int/2addr v1, v2

    .line 54
    array-length v2, p1

    if-ne v1, v2, :cond_13

    .line 55
    array-length v2, p1

    mul-int/lit8 v2, v2, 0x2

    new-array v2, v2, [B

    .line 56
    invoke-static {p1, v0, v2, v0, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 57
    nop

    .line 58
    move-object p1, v2

    goto :goto_13

    .line 61
    :cond_2a
    const/4 v2, 0x6

    new-array v2, v2, [B

    fill-array-data v2, :array_4e

    .line 62
    const/16 v3, 0xc

    new-array v3, v3, [B

    fill-array-data v3, :array_56

    .line 63
    invoke-static {p1, v1, v2}, Lcom/android/packageinstaller/VideoGuard;->indexOf([BI[B)I

    move-result v2

    if-gez v2, :cond_43

    invoke-static {p1, v1, v3}, Lcom/android/packageinstaller/VideoGuard;->indexOf([BI[B)I

    move-result p1
    :try_end_41
    .catchall {:try_start_4 .. :try_end_41} :catchall_48

    if-ltz p1, :cond_44

    :cond_43
    const/4 v0, 0x1

    .line 65
    :cond_44
    invoke-virtual {p0}, Ljava/io/InputStream;->close()V

    .line 63
    return v0

    .line 65
    :catchall_48
    move-exception p1

    invoke-virtual {p0}, Ljava/io/InputStream;->close()V

    .line 66
    throw p1

    nop

    :array_4e
    .array-data 1
        0x76t
        0x69t
        0x64t
        0x65t
        0x6ft
        0x2ft
    .end array-data

    nop

    :array_56
    .array-data 1
        0x76t
        0x0t
        0x69t
        0x0t
        0x64t
        0x0t
        0x65t
        0x0t
        0x6ft
        0x0t
        0x2ft
        0x0t
    .end array-data
.end method

